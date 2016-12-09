package se.liu.imt.mi.eee;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import openEHR.v1.template.TEMPLATE;

import org.openehr.am.archetype.Archetype;
import org.openehr.am.template.Flattener;
import org.openehr.am.template.OETParser;

import se.acode.openehr.parser.ADLParser;
import se.acode.openehr.parser.ParseException;

public class ArchetypeAndTemplateRepository {

	private Map<String, Archetype> archetypeMap = null;

	private Flattener flattener;
	private Map<String, TEMPLATE> templateMap = null;

	public ArchetypeAndTemplateRepository(File archetypeDirectory,
			final String archetypeFileExt) throws ParseException,
			FileNotFoundException, Exception {
		super();
		flattener = new Flattener();
		archetypeMap = new HashMap<String, Archetype>();
		Collection<File> fileList = listFiles(archetypeDirectory,
				new FileFilter() {
					public boolean accept(File pathname) {
						return pathname.getName().toLowerCase()
								.endsWith(archetypeFileExt);
					}
				}, true);
		for (File aFile : fileList) {
			ADLParser adlParser = new ADLParser(new FileInputStream(aFile),
					"UTF-8");
			Archetype archetype = null;
			archetype = adlParser.parse();
			if (archetype != null)
				archetypeMap.put(archetype.getArchetypeId().toString(),
						archetype);
		}
	}

	public ArchetypeAndTemplateRepository(File archetypeDirectory,
			final String archetypeFileExt, File templateDirectory,
			final String templateFileExt) throws ParseException,
			FileNotFoundException, Exception {
		this(archetypeDirectory, archetypeFileExt);
		templateMap = new HashMap<String, TEMPLATE>();
		try {
			Collection<File> fileList = listFiles(templateDirectory,
					new FileFilter() {
						public boolean accept(File pathname) {
							return pathname.getName().toLowerCase()
									.endsWith(templateFileExt);
						}
					}, true);
			for (File tFile : fileList) {
				OETParser oetParser = new OETParser();
				TEMPLATE templ = null;
				try {
					templ = oetParser.parseTemplate(new FileInputStream(tFile))
							.getTemplate();
				} catch (Exception e) {
					System.out
							.println("InstanceBuilder.main(): Parse error for template file "
									+ tFile.getName());
					e.printStackTrace(System.out);
				}
				if (templ != null)
					templateMap.put(templ.getId(), templ);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public Archetype flattenTemplate(TEMPLATE template) throws Exception {
		Archetype flattened = flattener.toFlattenedArchetype(template,
				archetypeMap, null);
		return flattened;
	}

	public Archetype getArchetype(String archetypeId) {
		return getArchetypeMap().get(archetypeId);
	}

	public Map<String, Archetype> getArchetypeMap() {
		return archetypeMap;
	}

	public TEMPLATE getTemplate(String templateId) {
		return getTemplateMap().get(templateId);
	}

	public Map<String, TEMPLATE> getTemplateMap() {
		return templateMap;
	}

	public static Collection<File> listFiles(File directory, FileFilter filter,
			boolean recurse) {
		// List of files / directories
		Vector<File> files = new Vector<File>();

		// Get files / directories in the directory
		File[] entries = directory.listFiles();

		// Go over entries
		for (File entry : entries) {
			// If there is no filter or the filter accepts the
			// file / directory, add it to the list
			if (filter == null || filter.accept(entry)) {
				files.add(entry);
			}

			// If the file is a directory and the recurse flag
			// is set, recurse into the directory
			if (recurse && entry.isDirectory()) {
				files.addAll(listFiles(entry, filter, recurse));
			}
		}

		// Return collection of files
		return files;
	}

}
