package br.uerj.lampada.openehr.susbuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;
import br.uerj.lampada.openehr.susbuilder.database.DBHandler;
import br.uerj.lampada.openehr.susbuilder.database.ReadInstance;

public class ReadInstanceTest extends TestCase {

	private DBHandler db;
	private String dbUrl;
	private String password;
	private String username;

	public ReadInstanceTest() throws IOException {
		Properties props = new Properties();
		props.load(ReadInstanceTest.class
				.getResourceAsStream("/susdb.properties"));
		dbUrl = props.getProperty("url");
		username = props.getProperty("username");
		password = props.getProperty("password");
		db = new DBHandler(dbUrl, username, password);
	}

	public void testClassName() throws Exception {
		assertEquals(DBHandler.POSTGRESQL_CLASS, db.getClassName());
	}

	public void testConnection() throws Exception {
		db = new DBHandler(dbUrl, username, password);
		assertTrue(db.getConnection().isValid(0));
	}

	public void testPatientNumber() throws Exception {
		ReadInstance ri = new ReadInstance(false);
		ri.patientId(false, db, 1000);
		List<String> results = ri.retrieveSingleResultToList();
		assertEquals(1000, results.size());
	}

	public void testRetrieveLoH() throws Exception {
		ReadInstance ri = new ReadInstance(false);
		ri.patientId(true, db, 10);
		List<HashMap<String, Object>> results = ri.retrieveResultsToLoH();
		assertTrue(results.get(0).containsKey(ReadInstance.PATIENT_ID_COL));
	}
}
