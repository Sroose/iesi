package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.DatabaseConnection;

/**
 * Connection object for MySQL databases. This class extends the default database connection object.
 * 
 * @author peter.billen
 *
 */
public class MysqlDatabaseConnection extends DatabaseConnection {

	private static String type = "mysql";

	public MysqlDatabaseConnection(String connectionURL, String userName, String userPassword) {
		super(type, connectionURL, userName, userPassword);
	}

	public MysqlDatabaseConnection(String hostName, int portNumber, String schemaName, String userName,
			String userPassword) {
		super(type, "jdbc:mysql://" + hostName + ":" + portNumber + "/" + schemaName, userName, userPassword);
	}
}
