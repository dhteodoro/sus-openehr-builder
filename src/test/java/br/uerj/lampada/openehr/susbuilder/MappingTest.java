package br.uerj.lampada.openehr.susbuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.collections.list.SetUniqueList;
import org.apache.commons.collections.map.MultiKeyMap;
import org.openehr.am.template.FlatteningException;
import org.openehr.rm.datatypes.basic.ReferenceModelName;

import br.uerj.lampada.openehr.susbuilder.builder.TemplateManager;
import br.uerj.lampada.openehr.susbuilder.database.DBHandler;
import br.uerj.lampada.openehr.susbuilder.database.ReadInstance;
import br.uerj.lampada.openehr.susbuilder.mapping.PathMapping;
import br.uerj.lampada.openehr.susbuilder.mapping.Mapping;
import br.uerj.lampada.openehr.susbuilder.mapping.PathMetadata;
import br.uerj.lampada.openehr.susbuilder.terminology.Terminology;
import br.uerj.lampada.openehr.susbuilder.utils.Constants;

// all paths are mapped or mapped paths exist
// dv_coded_text has a terminology
// quantity has units
// results from database exist in mapping terminology
// results not mapped in terminology are null values

public class MappingTest extends TestCase {

	private static String archetypeRepository;
	private static String templateRepository;
	private static String terminologyFile;
	
	private static String builderConfig = "builder.properties";

	private static Terminology terminology = new Terminology();
	static {
		
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(builderConfig));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		archetypeRepository = props.getProperty("archetype.repository");		
		templateRepository = props.getProperty("template.repository");
		terminologyFile = props.getProperty("terminology.file");
		
		try {
			terminology.load(MappingTest.class.getResourceAsStream(terminologyFile));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	private DBHandler db;

	private List<MultiKey> orderedKey;

	private PathMapping pathMapping;

	public MappingTest() {
		Properties props = new Properties();
		try {
			props.load(MappingTest.class
					.getResourceAsStream(builderConfig));
		} catch (IOException e) {
			e.printStackTrace();
		}

		String url = props.getProperty("db.url");
		String username = props.getProperty("db.username");
		String password = props.getProperty("db.password");

		db = new DBHandler(url, username, password);
	}

	private List<String> hasPathTerminology(String template) {
		List<String> paths = new ArrayList<String>();
		for (String path : Mapping.getColumnMap(template).keySet()) {
			PathMetadata pm = pathMapping.getPathMapping().get(path);
			if (ReferenceModelName.DV_CODED_TEXT.getName().equals(
					pm.getDataType())
					&& pm.getTerminologyName() == null) {
				paths.add(path);
			}
		}
		return paths;
	}

	private List<String> hasPathUnit(String template) {
		List<String> paths = new ArrayList<String>();
		for (String path : Mapping.getColumnMap(template).keySet()) {
			PathMetadata pm = pathMapping.getPathMapping().get(path);
			if (ReferenceModelName.DV_QUANTITY.getName().equals(
					pm.getDataType())
					&& pm.getUnit() == null) {
				paths.add(path);
			}
		}
		return paths;
	}

	private MultiKeyMap isDataMapped(String template) {
		orderedKey = SetUniqueList.decorate(new ArrayList<MultiKey>());
		MultiKeyMap testResult = new MultiKeyMap();

		boolean isAIH = false;
		if (!Constants.templatesAPAC.contains(template)) {
			isAIH = true;
		}
		ReadInstance ri = new ReadInstance(isAIH);
		for (String path : Mapping.getColumnMap(template).keySet()) {
			PathMetadata pm = pathMapping.getPathMapping().get(path);
			String termName = pm.getTerminologyName();
			if (termName != null) {
				Object column = pm.getColumn();
				try {
					String testCol = null;
					if (column instanceof List) {
						testCol = ((List<String>) column).get(0);
					} else {
						testCol = (String) column;
					}
					ri.column(template, db, testCol);
				} catch (SQLException e) {
					e.printStackTrace();
				}

				List<String> results = ri.retrieveSingleResultToList();
				for (String code : results) {
					if ((!code.trim().equals(""))
							&& terminology.getText(termName, code) == null
							&& (pm.getNullValues() == null || !pm
									.getNullValues().contains(code.trim()))) {
						orderedKey.add(new MultiKey(termName, code));
						testResult.put(termName, code, column);
					}
				}
			}
		}
		return testResult;
	}

	private List<String> isPathMapped(String template) {
		List<String> paths = new ArrayList<String>();
		for (String path : Mapping.getColumnMap(template).keySet()) {
			if (!pathMapping.getPathMapping().containsKey(path)) {
				paths.add(path);
			}
		}
		return paths;
	}

	private void printPaths(String template, List<String> paths, String text) {
		if (paths.size() > 0) {
			if (text == null) {
				text = "Failed paths";
			}

			if (paths.size() > 0) {
				System.out.println("################");
				System.out.println(text);
				for (String path : paths) {
					System.out.println(template + "::" + path);
				}
			}
		}
	}

	private void printTerminologyMapping(String template, MultiKeyMap results,
			String text) {

		if (results.keySet().size() > 0) {
			System.out.println("################");
			System.out.println(text);

			for (MultiKey key : orderedKey) {
				String termName = (String) key.getKey(0);
				String code = (String) key.getKey(1);
				String column = (String) results.get(key);
				System.out.println(template + "::" + column
						+ " is missing map for " + code + " in terminology "
						+ termName);
			}
		}

	}

	private void setPathMap(String template) {

		String templateFilename = templateRepository + "/" + template + ".oet"; // Reads
																				// template
		TemplateManager tc;
		boolean isPersistent = false;
		if (template.equals(Constants.DEMOGRAPHIC_DATA)) {
			isPersistent = true;
		}

		try {
			tc = new TemplateManager(templateFilename, archetypeRepository,
					templateRepository, isPersistent);
			pathMapping = new PathMapping(template, tc.getArchetype(),
					tc.getArchetypeMap());
		} catch (FlatteningException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void test(String template) {
		List<String> paths = isPathMapped(template);
		printPaths(template, paths, "Path missing map");
		assertTrue(paths.size() == 0);

		paths = hasPathTerminology(template);
		printPaths(template, paths, "Coded path missing terminology");
		assertTrue(paths.size() == 0);

		paths = hasPathUnit(template);
		printPaths(template, paths, "Quantity path missing unit");
		assertTrue(paths.size() == 0);

		// Enable this to test full database mapping
//		MultiKeyMap results = isDataMapped(template);
//		printTerminologyMapping(template, results,
//				"Data missing terminology map");
//		assertTrue(results.isEmpty());
	}

	public void testBariatricSurgery() throws Exception {
		String template = Constants.BARIATRIC_SURGERY;
		setPathMap(template);
		test(template);
	}

	public void testChemotherapy() throws Exception {
		String template = Constants.CHEMOTHERAPY;
		setPathMap(template);
		test(template);
	}

	public void testDemographicData() throws Exception {
		String template = Constants.DEMOGRAPHIC_DATA;
		setPathMap(template);
		test(template);
	}

	public void testHospitalization() throws Exception {
		String template = Constants.HOSPITALIZATION;
		setPathMap(template);
		test(template);
	}

	public void testMedicines() throws Exception {
		String template = Constants.MEDICINES;
		setPathMap(template);
		test(template);
	}

	public void testNephrology() throws Exception {
		String template = Constants.NEPHROLOGY;
		setPathMap(template);
		test(template);
	}

	public void testOutpatientVarious() throws Exception {
		String template = Constants.MISCELLANEOUS;
		setPathMap(template);
		test(template);
	}

	public void testRadiotherapy() throws Exception {
		String template = Constants.RADIOTHERAPY;
		setPathMap(template);
		test(template);
	}
}
