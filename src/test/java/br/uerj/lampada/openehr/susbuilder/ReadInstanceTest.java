package br.uerj.lampada.openehr.susbuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;
import br.uerj.lampada.openehr.susbuilder.database.DBHandler;
import br.uerj.lampada.openehr.susbuilder.database.ReadInstance;
import br.uerj.lampada.openehr.susbuilder.utils.Constants;

public class ReadInstanceTest extends TestCase {

	private DBHandler db;
	private String dbUrl;
	private String password;
	private String username;

	public ReadInstanceTest(String name) throws IOException {
		super(name);
	}

	public void testClassName() throws Exception {
		assertEquals(DBHandler.POSTGRESQL_CLASS, db.getClassName());
	}

	public void testConnection() throws Exception {
		db = new DBHandler(dbUrl, username, password);
		assertTrue(db.getConnection().isValid(0));
	}

	/*
	 * public void testPatientNumber() throws Exception { ReadInstance ri = new
	 * ReadInstance(false); ri.patientId(false, db, 1000); List<String> results
	 * = ri.retrieveSingleResultToList(); assertEquals(1000, results.size()); }
	 */

	public void testRetrieveLoH() throws Exception {
		ReadInstance ri = new ReadInstance(false, db);
		ri.patientId(true, 10);
		List<HashMap<String, Object>> results = ri.retrieveResultsToLoH();
		assertTrue(results.get(0).containsKey(ReadInstance.PATIENT_ID_COL));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void setUp() throws Exception {
		Properties props = new Properties();
		props.load(new FileInputStream(Constants.builderConfig));

		dbUrl = props.getProperty("db.url");
		username = props.getProperty("db.username");
		password = props.getProperty("db.password");
		db = new DBHandler(dbUrl, username, password);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
