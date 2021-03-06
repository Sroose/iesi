package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.DatabaseConnection;

/**
 * Connection object for SQLite databases. This class extends the default database connection object.
 * 
 * @author peter.billen
 *
 */
public class SqliteDatabaseConnection extends DatabaseConnection {

	private static String type = "sqlite";

	public SqliteDatabaseConnection(String connectionURL, String userName, String userPassword) {
		super(type, connectionURL, userName, userPassword);
	}

	public SqliteDatabaseConnection(String fileName) {
		super(type, "jdbc:sqlite:" + fileName, "", "");
	}

}
