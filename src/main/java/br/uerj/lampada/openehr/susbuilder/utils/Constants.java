/**
 * 
 */
package br.uerj.lampada.openehr.susbuilder.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import br.uerj.lampada.openehr.susbuilder.terminology.Terminology;

/**
 * @author teodoro
 * 
 */
public abstract class Constants {

	public static String archetypeRepository;

	public static final String BARIATRIC_SURGERY = "bariatric_surgery";
	public static String builderConfig = "builder.properties";
	public static final String CHEMOTHERAPY = "chemotherapy";
	public static final String COMPOSITION_STR = "composition";
	public static final String COMPOSITION_UUID_PREFIX = "1.2.";
	public static final String CONTRIBUTION_STR = "contribution";
	public static final String CONTRIBUTION_UUID_PREFIX = "1.3.";
	public static final String DEMOGRAPHIC_DATA = "demographic_data";

	public static final String EHR_STR = "ehr";
	public static final String EHR_UUID_PREFIX = "1.1.1.";
	public static final String EHRACCESS_STR = "ehr_access";
	public static final String EHRACCESS_UUID_PREFIX = "1.4.1";
	public static final String EHRSTATUS_STR = "ehr_status";

	public static final String EHRSTATUS_UUID_PREFIX = "1.5.1";
	public static final String HOSPITALISATION = "hospitalisation";
	public static String logConfig;
	public static int logStep;
	public static final String MEDICATION = "medication";
	public static final String MISCELLANEOUS = "outpatient_miscellaneous";

	public static final String NEPHROLOGY = "nephrology";

	public static final String RADIOTHERAPY = "radiotherapy";

	public static String templateRepository;

	public static final List<String> templatesAIH = new ArrayList<String>();

	public static final List<String> templatesAPAC = new ArrayList<String>();

	public static Terminology terminology = new Terminology();
	public static final String VERSION_STR = "version";

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

		logConfig = props.getProperty("log.config");

		logStep = Integer.parseInt(props.getProperty("log.step"));

		templatesAPAC.add(BARIATRIC_SURGERY);
		templatesAPAC.add(CHEMOTHERAPY);
		templatesAPAC.add(MEDICATION);
		templatesAPAC.add(NEPHROLOGY);
		templatesAPAC.add(MISCELLANEOUS);
		templatesAPAC.add(RADIOTHERAPY);
		templatesAPAC.add(DEMOGRAPHIC_DATA);

		templatesAIH.add(HOSPITALISATION);
		templatesAIH.add(DEMOGRAPHIC_DATA);

		try {
			terminology.load(new FileInputStream(props
					.getProperty("terminology.file")));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
