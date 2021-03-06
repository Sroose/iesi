package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.ConnectionParameter;

public class ConnectionParameterConfiguration {

	private FrameworkExecution frameworkExecution;
	private ConnectionParameter connectionParameter;

	// Constructors
	public ConnectionParameterConfiguration(ConnectionParameter connectionParameter, FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
		this.setConnectionParameter(connectionParameter);
	}

	public ConnectionParameterConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}
	
	// Insert
	public String getInsertStatement(String connectionName, String environmentName) {
		String sql = "";

		sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("ConnectionParameters");
		sql += " (CONN_NM, ENV_NM, CONN_PAR_NM, CONN_PAR_VAL) ";
		sql += "VALUES ";
		sql += "(";
		sql += SQLTools.GetStringForSQL(connectionName);
		sql += ",";
		sql += SQLTools.GetStringForSQL(environmentName);
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getConnectionParameter().getName());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getConnectionParameter().getValue());
		sql += ")";
		sql += ";";

		return sql;
	}
	
	public ConnectionParameter getConnectionParameter(String cpnnectionName, String environmentName, String connectionParameterName) {
		ConnectionParameter connectionParameter = new ConnectionParameter();
		CachedRowSet crsConnectionParameter = null;
		String queryConnectionParameter = "select CONN_NM, ENV_NM, CONN_PAR_NM, CONN_PAR_VAL from " + this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("ConnectionParameters")
				+ " where CONN_NM = '" + cpnnectionName + "' and CONN_PAR_NM = '" + connectionParameterName + "' and ENV_NM = '" + environmentName + "'";
		crsConnectionParameter = this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().executeQuery(queryConnectionParameter);
		try {
			while (crsConnectionParameter.next()) {
				connectionParameter.setName(connectionParameterName);
				connectionParameter.setValue(crsConnectionParameter.getString("CONN_PAR_VAL"));

			}
			crsConnectionParameter.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		return connectionParameter;
	}
	
	public String getConnectionParameterValue(String connectionName, String environmentName,
			String connectionParameterName) {
		String output = "";
		CachedRowSet crsConnectionParameter = null;
		String queryConnectionParameter = "select CONN_NM, ENV_NM, CONN_PAR_NM, CONN_PAR_VAL from "
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("ConnectionParameters") + " where CONN_NM = '"
				+ connectionName + "' and ENV_NM = '" + environmentName + "' and CONN_PAR_NM = '" + connectionParameterName + "'";
		crsConnectionParameter = this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().executeQuery(queryConnectionParameter);
		try {
			while (crsConnectionParameter.next()) {
				output = crsConnectionParameter.getString("CONN_PAR_VAL");
			}
			crsConnectionParameter.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}

		return output;
	}


	// Getters and Setters
	public ConnectionParameter getConnectionParameter() {
		return connectionParameter;
	}

	public void setConnectionParameter(ConnectionParameter connectionParameter) {
		this.connectionParameter = connectionParameter;
	}
	
	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

}