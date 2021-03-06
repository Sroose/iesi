package io.metadew.iesi.util.harvest;

import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.DatabaseConnection;
import io.metadew.iesi.connection.operation.ConnectionOperation;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.framework.execution.FrameworkExecutionContext;
import io.metadew.iesi.metadata.configuration.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.metadata.definition.Context;

public class DatabaseOffloadExecution
{

	private FrameworkExecution frameworkExecution;

	// Constructors
	public DatabaseOffloadExecution()
	{
		Context context = new Context();
		context.setName("offload");
		context.setScope("");
		this.setFrameworkExecution(new FrameworkExecution(new FrameworkExecutionContext(context)));
	}

	// Methods
	public void offloadData(String sourceConnectionName, String sourceEnvironmentName, String targetConnectionName,
				String targetEnvironmentName, String sqlStatement, String name, boolean cleanPrevious)
	{

		// Get Connection
		ConnectionOperation connectionOperation = new ConnectionOperation(this.getFrameworkExecution());
		ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(this.getFrameworkExecution());

		Connection sourceConnection = connectionConfiguration.getConnection(sourceConnectionName, sourceEnvironmentName);
		DatabaseConnection sourceDatabaseConnection = connectionOperation.getDatabaseConnection(sourceConnection);
		Connection targetConnection = connectionConfiguration.getConnection(targetConnectionName, targetEnvironmentName);
		DatabaseConnection targetDatabaseConnection = connectionOperation.getDatabaseConnection(targetConnection);

		CachedRowSet crs = null;
		crs = sourceDatabaseConnection.executeQuery(sqlStatement);

		String QueryString = "";
		try
		{
			// Get result set meta data
			ResultSetMetaData rsmd = crs.getMetaData();
			int cols = rsmd.getColumnCount();

			// Determine name
			if (name == null || name.isEmpty())
			{
				name = rsmd.getTableName(1);
			}

			// Cleaning
			if (cleanPrevious)
			{
				QueryString = SQLTools.getDropStmt(name, true);
				targetDatabaseConnection.executeUpdate(QueryString);
			}

			// create the dataset table if needed
			QueryString = SQLTools.getCreateStmt(rsmd, name, true);
			targetDatabaseConnection.executeUpdate(QueryString);

			String temp = "";
			String sql = SQLTools.getInsertPstmt(rsmd, name);
			targetDatabaseConnection.createLiveConnection();
			PreparedStatement preparedStatement = targetDatabaseConnection.createLivePreparedStatement(sql);

			int crsType = crs.getType();
			if (crsType != java.sql.ResultSet.TYPE_FORWARD_ONLY)
			{
				crs.beforeFirst();
			}

			while (crs.next())
			{
				for (int i = 1; i < cols + 1; i++)
				{
					temp = crs.getString(i);
					preparedStatement.setString(i, temp);
				}
				preparedStatement.executeUpdate();
			}
		}
		catch (Exception e)
		{
			System.out.println(QueryString);
			System.out.println("Query Actions Failed");
			e.printStackTrace();
		}
		finally
		{
			targetDatabaseConnection.closeLiveConnection();
		}

	}

	// Getters and setters
	public FrameworkExecution getFrameworkExecution()
	{
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution)
	{
		this.frameworkExecution = frameworkExecution;
	}
}