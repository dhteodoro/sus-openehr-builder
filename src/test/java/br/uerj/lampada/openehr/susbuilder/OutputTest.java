package br.uerj.lampada.openehr.susbuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import junit.framework.TestCase;
import br.uerj.lampada.openehr.susbuilder.database.DBHandler;
import br.uerj.lampada.openehr.susbuilder.terminology.Terminology;

public class OutputTest extends TestCase {
	
	private static String builderConfig = "builder.properties";

	private static final String aihPatientFile = "./patients/test/aih.txt";
	private static final String apacPatientFile = "./patients/test/apac.txt";

	private static final String outputFolder = "./ehr";
	private DBHandler db;

	private Terminology terminology;

	public OutputTest() {
		
		Properties props = new Properties();
		try {
			props.load(OutputTest.class
					.getResourceAsStream(builderConfig));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String terminologyFile = props.getProperty("terminology.file");
		
		terminology = new Terminology();
		try {
			terminology.load(OutputTest.class.getResourceAsStream(terminologyFile));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		
		String dbUrl = props.getProperty("db.url");
		String username = props.getProperty("db.username");
		String password = props.getProperty("db.password");
		db = new DBHandler(dbUrl, username, password);
	}

	public void testReadOutput() throws Exception {
		assertTrue(true);
	}

	public void testWriteOutput() throws Exception {
		EHRGenerator builder = new EHRGenerator("ehr", "xml", outputFolder,false);
		
		// read the list of patients from the input file
		List<String> patients = FileUtils.readLines(new File(apacPatientFile));
		
		// create APAC
		builder.generateEHRs(patients);

		builder = new EHRGenerator("ehr", "xml", outputFolder,true);
		
		// read the list of patients from the input file
		patients = FileUtils.readLines(new File(aihPatientFile));
	
		// create AIH
		builder.generateEHRs(patients);
	}
}
