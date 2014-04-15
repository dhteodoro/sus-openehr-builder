/**
 * 
 */
package br.uerj.lampada.openehr.susbuilder.builder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.openehr.am.template.FlatteningException;
import org.openehr.rm.composition.Composition;

import br.uerj.lampada.openehr.susbuilder.database.DBHandler;
import br.uerj.lampada.openehr.susbuilder.database.ReadInstance;
import br.uerj.lampada.openehr.susbuilder.utils.Constants;

/**
 * @author teodoro
 * 
 */
public class CompositionManager {

	private static Logger log = Logger.getLogger(CompositionManager.class);

	private Map<String, CompositionContent> compositionContent;

	private DBHandler dbHandler;

	private boolean isAIH;

	private Map<String, TemplateManager> templateManager;

	private List<String> templates;

	public CompositionManager(Map<String, TemplateManager> templateManager,
			Map<String, CompositionContent> compositionContent, boolean isAIH) {
		this.templateManager = templateManager;
		this.compositionContent = compositionContent;
		this.isAIH = isAIH;
		this.dbHandler = connectDB();
		this.templates = Constants.templatesAPAC;
		if (isAIH) {
			templates = Constants.templatesAIH;
		}
	}

	/**
	 * Retrieve the values from the database that are going to be used to fill
	 * in the composition
	 * 
	 * @param db
	 * @param template
	 * @param isAIH
	 * @param patientId
	 * @return
	 * @throws SQLException
	 */
	private List<HashMap<String, Object>> retrieveCompositionContent(
			DBHandler db, String template, boolean isAIH, String patientId)
			throws SQLException {
		List<HashMap<String, Object>> results = new ArrayList<HashMap<String, Object>>();

		ReadInstance ri = new ReadInstance(isAIH, template);

		try {
			ri.queryTemplate(db, patientId);
		} catch (Exception e) {
			log.error("Cannot retrieve composition content: "+ e.getMessage());
		}

		results = ri.retrieveResultsToLoH();
		return results;
	}

	/**
	 * Create the compositions for a given patient
	 * 
	 * @param patient
	 * @param uuid
	 * @return
	 * @throws FlatteningException
	 * @throws Exception
	 */
	public List<Composition> buildCompositions(String patient, String uuid)
			throws FlatteningException, Exception {

		List<Composition> compositions = new ArrayList<Composition>();

		// Retrieve results for a given patient
		String patientId = Constants.EHR_UUID_PREFIX + uuid;
		// Populate a template
		int count = 1;
		for (String template : templates) {
			// Retrieve template data
			List<HashMap<String, Object>> dbResult = retrieveCompositionContent(
					dbHandler, template, isAIH, patient);

			if (dbResult == null || dbResult.isEmpty()) {
				continue;
			}

			CompositionContent inst = compositionContent.get(template);
			TemplateManager build = templateManager.get(template);

			for (HashMap<String, Object> dbValues : dbResult) {
				// Populate the compositions
				String uid = Constants.COMPOSITION_UUID_PREFIX + "" + count
						+ "." + uuid;
				try {
					inst.updateContent(dbValues);
					build.createComposition(patientId, uid, inst);

					// Composition comp = build.getComposition();
					//
					// Object compm = new Object();
					// if(comp instanceof Cloneable) {
					// compm = comp.clone();
					// }
					//
					// compositions.add((Composition) compm);

					compositions.add(build.getComposition());
					count++;
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println(e.getMessage());
				}
			}
		}

		return compositions;
	}

	/**
	 * Connect to the AIH/APAC databases
	 * 
	 * @return
	 * @throws IOException
	 */
	private static DBHandler connectDB() {
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(Constants.builderConfig));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String url = props.getProperty("db.url");
		String username = props.getProperty("db.username");
		String password = props.getProperty("db.password");

		DBHandler dbHandler = new DBHandler(url, username, password);

		try {
			if (!dbHandler.getConnection().isValid(0)) {
				log.error("Connection not valid");
				System.exit(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("Cannot connect to the source database");
			System.exit(1);
		}

		return dbHandler;
	}

	/**
	 * Method used to convert a SUS patient ids (or authorization ids) in a UUID
	 * The ids coming from the SUS database are in two formats: hash sequence
	 * (APAC) and integer sequence (AIH) E.g.: Hash ----> ƒ„ƒ{{}{‚€�^?€€^?|
	 * Integer -> 4110106631334
	 * 
	 * @param text
	 * @return
	 * @throws Exception
	 */
	public static String dumpString(String text) throws Exception {
		// 2.39.\d+.\d+.\d+.\d+.\d+
		String uuid = "";
		if (text.matches("^\\d+$")) {
			uuid = text;
		} else {
			for (int i = 0; i < text.length(); i++) {
				uuid += (int) text.charAt(i);
			}
		}

		if (uuid.length() / 5 < 2) {
			while (uuid.length() / 5 < 2) {
				uuid = "0" + uuid;
			}
		}
		int div = uuid.length() / 5 - 1;
		Matcher uuidMatcher = Pattern.compile(
				"^(\\d{" + div + "})(\\d{" + div + "})(\\d{" + div + "})(\\d{"
						+ div + "})(\\d.*)$").matcher(uuid);
		if (uuidMatcher.find()) {
			uuid = uuidMatcher.group(1) + "." + uuidMatcher.group(2) + "."
					+ uuidMatcher.group(3) + "." + uuidMatcher.group(4) + "."
					+ uuidMatcher.group(5);
		} else {
			throw new Exception("Cannot match UUID format: " + uuid);
		}

		return uuid;
	}

}
