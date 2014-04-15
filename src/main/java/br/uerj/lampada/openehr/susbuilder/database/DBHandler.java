package br.uerj.lampada.openehr.susbuilder.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

/*
 * Class used to handle database connection and queries
 * 
 */
public class DBHandler {

	private static Connection connection;

	private static Logger log = Logger.getLogger(DBHandler.class);

	public final static String MYSQL = "mysql";
	public final static String MYSQL_CLASS = "com.mysql.jdbc.Driver";

	public final static String ORACLE = "oracle";
	public final static String ORACLE_CLASS = "oracle.jdbc.driver.OracleDriver";

	public final static String POSTGRESQL = "postgresql";
	public final static String POSTGRESQL_CLASS = "org.postgresql.Driver";

	public final static String SQLSERVER = "sqlserver";
	public final static String SQLSERVER_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

	private String dbUrl;

	private String driverName;
	private String password;
	private String sql;
	private String username;

	public DBHandler(String dbUrl, String username, String password) {
		this(null, dbUrl, username, password);
	}

	public DBHandler(String driverName, String dbUrl, String username,
			String password) {
		this.setDriverName(driverName);
		this.setDbUrl(dbUrl);
		this.setUsername(username);
		this.setPassword(password);
		this.connect();
	}

	public void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			log.error("Cannot close database connection: " + e.getMessage());
		}
	}

	public void connect() {
		connect(getDbUrl(), getUsername(), getPassword());
	}

	public void connect(String url, String username, String password) {
		try {
			if (connection == null || !connection.isValid(0)) {
				// STEP 1: Register JDBC driver
				if (driverName == null || driverName.length() == 0)
					driverName = getClassName();

				Class.forName(driverName);
				// STEP 2: Open a connection
				log.debug("Connecting to database...");
				connection = DriverManager.getConnection(url, username,
						password);
			}
		} catch (Exception e) {
			log.error("Cannot connect to database: "+ e.getMessage());
		}
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return getClassName(dbUrl);
	}

	/**
	 * @param url
	 *            the database connection url
	 * 
	 * @return the className
	 */
	public String getClassName(String url) {
		String className = null;

		if (getDBMS(url) == ORACLE)
			className = ORACLE_CLASS;
		else if (getDBMS(url) == POSTGRESQL)
			className = POSTGRESQL_CLASS;
		else if (getDBMS(url) == MYSQL)
			className = MYSQL_CLASS;
		else if (getDBMS(url) == SQLSERVER)
			className = SQLSERVER_CLASS;

		return className;
	}

	/**
	 * @return the connection
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * @return the dbUrl
	 */
	public String getDbUrl() {
		return dbUrl;
	}

	/**
	 * @return the driverName
	 */
	public String getDriverName() {
		return driverName;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return the sql
	 */
	public String getSql() {
		return sql;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	public ResultSet runQuery(String sql) throws SQLException {
		ResultSet result = null;
		log.debug("Running query...");
		log.debug(sql);
		try {
			long startTime = System.currentTimeMillis();
			connect();
			Statement statement = connection.createStatement();
			statement.execute(sql);
			result = statement.getResultSet();
			log.debug("Query time: " + (System.currentTimeMillis() - startTime)
					+ " ms");
		} catch (SQLException e) {
			log.error("Cannot execute query: "+ sql);
			throw e;
		}
		return result;
	}

	/**
	 * @param connection
	 *            the sql to connection
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	/**
	 * @param dbUrl
	 *            the sql to dbUrl
	 */
	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	/**
	 * @param driverName
	 *            the sql to driverName
	 */
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	/**
	 * @param password
	 *            the sql to password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @param sql
	 *            the sql to set
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}

	/**
	 * @param username
	 *            the sql to username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	public static String getDBMS(String url) {
		String dbms = null;

		if (url.startsWith("jdbc:" + ORACLE))
			dbms = ORACLE;
		else if (url.startsWith("jdbc:" + POSTGRESQL))
			dbms = POSTGRESQL;
		else if (url.startsWith("jdbc:" + MYSQL))
			dbms = MYSQL;
		else if (url.startsWith("jdbc:" + SQLSERVER))
			dbms = SQLSERVER;

		return dbms;
	}
}