package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.ImpersonationParameter;

public class ImpersonationParameterConfiguration {

	private ImpersonationParameter impersonationParameter;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public ImpersonationParameterConfiguration(ImpersonationParameter impersonationParameter, FrameworkExecution frameworkExecution) {
		this.setImpersonationParameter(impersonationParameter);
		this.setFrameworkExecution(frameworkExecution);
	}
	
	public ImpersonationParameterConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	// Insert
	public String getInsertStatement(String impersonationName) {
		String sql = "";
		
		sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("ImpersonationParameters");
		sql += " (IMP_NM, CONN_NM, CONN_IMP_NM, CONN_IMP_DSC) ";
		sql += "VALUES ";
		sql += "(";
		sql += SQLTools.GetStringForSQL(impersonationName);
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getImpersonationParameter().getConnection());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getImpersonationParameter().getImpersonatedConnection());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getImpersonationParameter().getDescription());
		sql += ")";
		sql += ";";

		return sql;
	}
	
	public ImpersonationParameter getImpersonationParameter(String impersonationName, String impersonationParameterName) {
		ImpersonationParameter impersonationParameter = new ImpersonationParameter();
		CachedRowSet crsImpersonationParameter = null;
		String queryImpersonationParameter = "select IMP_NM, CONN_NM, CONN_IMP_NM, CONN_IMP_DSC from " + this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("ImpersonationParameters")
				+ " where IMP_NM = '" + impersonationName + "' and CONN_NM = '" + impersonationParameterName + "'";
		crsImpersonationParameter = this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().executeQuery(queryImpersonationParameter);
		try {
			while (crsImpersonationParameter.next()) {
				impersonationParameter.setConnection(impersonationParameterName);
				impersonationParameter.setImpersonatedConnection(crsImpersonationParameter.getString("CONN_IMP_NM"));
				impersonationParameter.setDescription(crsImpersonationParameter.getString("CONN_IMP_DSC"));
			}
			crsImpersonationParameter.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		return impersonationParameter;
	}

	
	// Getters and Setters
	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

	public ImpersonationParameter getImpersonationParameter() {
		return impersonationParameter;
	}

	public void setImpersonationParameter(ImpersonationParameter impersonationParameter) {
		this.impersonationParameter = impersonationParameter;
	}

}