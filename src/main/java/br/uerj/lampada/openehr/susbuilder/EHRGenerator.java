package br.uerj.lampada.openehr.susbuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.PropertyConfigurator;
import org.openehr.am.template.FlatteningException;
import org.openehr.rm.composition.Composition;

import br.uerj.lampada.openehr.susbuilder.builder.CompositionContent;
import br.uerj.lampada.openehr.susbuilder.builder.CompositionManager;
import br.uerj.lampada.openehr.susbuilder.builder.TemplateManager;
import br.uerj.lampada.openehr.susbuilder.mapping.PathMapping;
import br.uerj.lampada.openehr.susbuilder.printer.EHRObjectPrinter;
import br.uerj.lampada.openehr.susbuilder.printer.impl.CompositionPrinter;
import br.uerj.lampada.openehr.susbuilder.printer.impl.EHRPrinter;
import br.uerj.lampada.openehr.susbuilder.printer.impl.VersionPrinter;
import br.uerj.lampada.openehr.susbuilder.utils.Constants;

/**
 * Class is used to generate compositions, versions and EHR RM instances from
 * APAC/AIH template files in the OET-format and template and archetype
 * repositories.
 * 
 * The input data should be stored in a relational database and a file with
 * patient ids should be provided as input
 * 
 * main() arguments:
 * 
 * EHRGenerator -p <patient_file> -o <output dir> -t <ehr|version|composition>
 * -f <json|xml> [--aih]
 * 
 * The output files are located in the "ehr", "ehrAccess", "ehrStatus",
 * "contribution" and "composition" directories within the specified output
 * directory and the format depends on the options: XML and JSON
 * 
 * Example from command line :
 * 
 * EHRGenerator -p ~/Documents/patients/ids.txt -o ~/Documents/ehr -t ehr -f xml
 * 
 * @author teodoro
 * 
 */
public class EHRGenerator {

	private Map<String, CompositionContent> compositionContents;

	private PrintWriter failedFile;

	private List<String> failedIds;
	private String format;

	private boolean isAIH;
	private String outputFolder;
	private CompositionManager populator;
	private EHRObjectPrinter printer;

	private PrintWriter succeededFile;
	private List<String> successfulIds;

	// Template skeletons
	private Map<String, TemplateManager> templateManagers;
	private String type;

	public EHRGenerator(String type, String format, String outputFolder,
			boolean isAIH) throws Exception {
		this.successfulIds = new ArrayList<String>();
		this.failedIds = new ArrayList<String>();

		this.type = type;
		this.format = format;
		this.outputFolder = outputFolder + "/" + format;
		this.isAIH = isAIH;

		this.templateManagers = new HashMap<String, TemplateManager>();
		this.compositionContents = new HashMap<String, CompositionContent>();

		this.createDirectories();
		this.initLogFiles();
		this.initTemplates();
	}

	/**
	 * Close log files and print ending messages
	 * 
	 */
	private void closeLog() {
		succeededFile.close();
		failedFile.close();

		Date date = new Date();

		if (successfulIds != null && !successfulIds.isEmpty()) {
			System.out.println("[" + new Timestamp(date.getTime())
					+ "] EHRGenerator created " + successfulIds.size()
					+ " EHRs successfully");
		}

		date = new Date();
		if (failedIds != null && !failedIds.isEmpty()) {
			System.out.println("[" + new Timestamp(date.getTime())
					+ "] EHRGenerator failed to create " + failedIds.size()
					+ " EHRs");
		}
	}

	/**
	 * Create output directory
	 * 
	 */
	private void createDirectories() {

		List<String> directories = new ArrayList<String>();
		if (type.equals(Constants.EHR_STR)) {
			directories.add(Constants.EHR_STR + "/" + Constants.EHR_STR);
			directories.add(Constants.EHR_STR + "/" + Constants.EHRACCESS_STR);
			directories.add(Constants.EHR_STR + "/" + Constants.EHRSTATUS_STR);
			directories
					.add(Constants.EHR_STR + "/" + Constants.COMPOSITION_STR);
			directories.add(Constants.EHR_STR + "/"
					+ Constants.CONTRIBUTION_STR);
		} else {
			directories.add(type);
		}

		for (String dir : directories) {
			File file = new File(outputFolder + "/" + dir);
			// if the directory does not exist, create it
			if (!file.exists()) {
				file.mkdirs();
			}
		}
	}

	/**
	 * Log files containing information of objects successfully and failed
	 * created
	 * 
	 */
	private void initLogFiles() {
		String patientFile = outputFolder + "/patient";

		String sf = patientFile + ".succeeded";
		String ff = patientFile + ".failed";

		File fileTemp = new File(sf);
		if (fileTemp.exists()) {
			fileTemp.delete();
		}
		fileTemp = new File(ff);
		if (fileTemp.exists()) {
			fileTemp.delete();
		}

		try {
			succeededFile = new PrintWriter(new BufferedWriter(new FileWriter(
					sf, true)));
			failedFile = new PrintWriter(new BufferedWriter(new FileWriter(ff,
					true)));
		} catch (Exception e) {
			System.err.println("Cannot create id log file:" + e.getMessage());
		}
	}

	/**
	 * Init the composition builder class for each template the the content
	 * structure
	 * 
	 * @param isAIH
	 * @throws Exception
	 */
	private void initTemplates() throws Exception {
		// define the templates to be used: APAC or AIH sets
		List<String> templates = Constants.templatesAPAC;
		if (isAIH) {
			templates = Constants.templatesAIH;
		}

		// Generate the template skeletons
		templateManagers = new HashMap<String, TemplateManager>();
		compositionContents = new HashMap<String, CompositionContent>();

		for (String template : templates) {
			boolean isPersistent = false;
			if (template.equals(Constants.DEMOGRAPHIC_DATA)) {
				isPersistent = true;
			}

			TemplateManager comp = new TemplateManager(template,
					Constants.archetypeRepository,
					Constants.templateRepository, isPersistent);
			CompositionContent inst = new CompositionContent(new PathMapping(
					template, comp.getArchetype(), comp.getArchetypeMap()),
					Constants.terminology);

			comp.cleanTemplate(inst.getPathState());

			templateManagers.put(template, comp);
			compositionContents.put(template, inst);
		}
	}

	public void generateEHRs(List<String> patients) throws FlatteningException,
			Exception {

		Date date = new Date();
		// initiate the classes to populate and print the ehr objects
		populator = new CompositionManager(templateManagers,
				compositionContents, isAIH);
		if (type.equals(Constants.EHR_STR)) {
			printer = new EHRPrinter(outputFolder, format);
		} else if (type.equals(Constants.VERSION_STR)) {
			printer = new VersionPrinter(outputFolder, format);
		} else if (type.equals(Constants.COMPOSITION_STR)) {
			printer = new CompositionPrinter(outputFolder, format);
		} else {
			System.err.println("Unknow EHR object: " + type);
			System.exit(1);
		}

		// for each patient id
		// 1) populate the patient objects (ehr, version or composition)
		// 2) write the object to a file

		int count = 1;
		for (String patient : patients) {
			try {

				String uuid = CompositionManager.dumpString(patient);
				List<Composition> compositions = populator.buildCompositions(
						patient, uuid);

				printer.writeOpenEHRObject(uuid, compositions);

				succeededFile.println(patient);
				successfulIds.add(patient);
			} catch (Exception e) {
				// e.printStackTrace();
				failedFile.println(patient);
				failedIds.add(patient);
				date = new Date();
				System.err.println("[" + new Timestamp(date.getTime()) + "] [" + patient + "]::" + e.getMessage());
			}

			if (count % Constants.logStep == 0) {
				date = new Date();
				System.out.println("[" + new Timestamp(date.getTime()) + "] " + count + " EHRs processed");
			}
			count++;
		}
		closeLog();
	}

	public static void main(String[] args) throws Exception {

		long startTime = System.currentTimeMillis();
		Date date = new Date();

		System.out.println("[" + new Timestamp(date.getTime())
				+ "] Starting to process EHRs");

		// configure log
		try {
			InputStream inStream = new FileInputStream(Constants.logConfig);
			Properties props = new Properties();
			props.load(inStream);
			PropertyConfigurator.configure(props);
		} catch (Exception e) {
			throw new RuntimeException(
					"Unable to load logging property for EHRGenerator.class... ");
		}

		Options options = new Options();

		// add options
		options.addOption("p", "patients", true, "File containing patient ids.");
		options.addOption("o", "ehr-dir", true,
				"Output directory (default ehr).");
		options.addOption("t", "type", true,
				"Type of output object. originally implemented: composition, version and ehr.");
		options.addOption("f", "format", true,
				"Output format. Orinally implemented: JSON and XML.");
		options.addOption("i", "aih", false,
				"If AIH data, APAC is assumed as default.");
		options.addOption("h", "help", false, "Print help statement.");

		// create CLI parser
		CommandLineParser cliParser = new GnuParser();
		CommandLine line = null;

		try {
			// parse the command line arguments
			line = cliParser.parse(options, args);
		} catch (ParseException exp) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + exp.getMessage());
			System.exit(-1);
		}

		if (line.hasOption("h") || line.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("EHRGenerator", options);
			System.exit(0);
		}

		// Check parameter availability
		if (!line.hasOption("p") && !line.hasOption("patients")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("EHRGenerator", options);
			System.exit(0);
		}

		// Read the parameters
		String patientFile = line.getOptionValue("patients").trim();

		// read the the directory for output
		String outputFolder = (line.hasOption("o") || line.hasOption("ehr-dir")) ? line
				.getOptionValue("ehr-dir").trim() : new String("./ehr_folder");

		String format = (line.hasOption("f") || line.hasOption("format")) ? line
				.getOptionValue("format").toLowerCase().trim() : new String("xml");

		String type = (line.hasOption("t") || line.hasOption("type")) ? line
				.getOptionValue("type").toLowerCase().trim() : new String("ehr");

		boolean isAIH = false;
		if (line.hasOption("i") || line.hasOption("aih")) {
			isAIH = true;
		}

		// read the list of patients from the input file
		List<String> patients = FileUtils.readLines(new File(patientFile));

		// build compositions
		EHRGenerator generator = new EHRGenerator(type, format, outputFolder,
				isAIH);

		// do the work
		generator.generateEHRs(patients);

		date = new Date();
		System.out.println("[" + new Timestamp(date.getTime()) + "] Finished "
				+ patients.size() + " EHRs in "
				+ (System.currentTimeMillis() - startTime) + "ms");

		System.exit(0);
	}

}
