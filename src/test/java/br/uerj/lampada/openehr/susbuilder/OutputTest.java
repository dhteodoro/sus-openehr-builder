package br.uerj.lampada.openehr.susbuilder;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;

import br.uerj.lampada.openehr.susbuilder.builder.CompositionManager;

public class OutputTest extends TestCase {

	private static final String aihPatientFile = "./test/patients/aih/aih.txt";
	private static final String apacPatientFile = "./test/patients/apac/apac.txt";

	private static final String outputFolder = "./test/ehr/output";

	public OutputTest(String name) throws Exception {
		super(name);
	}

	public void testReadOutput() throws Exception {
		assertTrue(true);
	}

	public void testWriteAIHOutput() throws Exception {
		EHRGenerator builder = new EHRGenerator("ehr", "xml", outputFolder,
				true);

		// read the list of patients from the input file
		List<String> patients = FileUtils.readLines(new File(aihPatientFile));

		// create AIH
		builder.generateEHRs(patients, 2000);
	}

	public void testCreateAIHUuid() throws Exception {
		// read the list of patients from the input file
		List<String> patients = FileUtils.readLines(new File(aihPatientFile));

		// test UUID generation
		for (String patient : patients) {
			String uuid = CompositionManager.dumpString(patient);
			String[] uuidParts = uuid.split("\\.");
			for (int i = 0; i < uuidParts.length; i++) {
				assert (uuidParts[i].length() > 1);
			}
		}
	}

	public void testWriteAPACOutput() throws Exception {
		EHRGenerator builder = new EHRGenerator("ehr", "xml", outputFolder,
				false);

		// read the list of patients from the input file
		List<String> patients = FileUtils.readLines(new File(apacPatientFile));

		// create APAC
		builder.generateEHRs(patients, 500);
	}

	public void testCreateAPACUuid() throws Exception {
		// read the list of patients from the input file
		List<String> patients = FileUtils.readLines(new File(apacPatientFile));

		// test UUID generation
		for (String patient : patients) {
			String uuid = CompositionManager.dumpString(patient);
			String[] uuidParts = uuid.split("\\.");
			for (int i = 0; i < uuidParts.length; i++) {
				assert (uuidParts[i].length() > 1);
			}
		}
	}
}
