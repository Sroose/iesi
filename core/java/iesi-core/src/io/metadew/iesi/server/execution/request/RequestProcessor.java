package io.metadew.iesi.server.execution.request;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.framework.execution.FrameworkExecution;

public class RequestProcessor {

	private FrameworkExecution frameworkExecution;
	public CachedRowSet crs;
	// fields
	public int prc_id;
	public int que_id;
	public String request_type;
	public String eng_cfg;
	public String env_nm;
	public String script_nm;
	public int context_id;
	public int scope_id;

	public RequestProcessor(FrameworkExecution frameworkExecution, int prc_id, int que_id) {
		this.setFrameworkExecution(frameworkExecution);
		this.prc_id = prc_id;
		this.que_id = que_id;
		this.setProcessor();
		this.getFields();
	}

	public void setProcessor() {
		String QueryString = "update " + this.getFrameworkExecution().getExecutionServerRepositoryConfiguration().getMetadataTableConfiguration().getPRC_CTL()
				+ " set request_id = " + this.que_id + " where prc_id = " + this.prc_id;
		this.getFrameworkExecution().getExecutionServerRepositoryConfiguration().executeUpdate(QueryString);

		QueryString = "update " + this.getFrameworkExecution().getExecutionServerRepositoryConfiguration().getMetadataTableConfiguration().getPRC_REQ()
				+ " set prc_id = " + this.prc_id + " where request_id = " + this.que_id;
		this.getFrameworkExecution().getExecutionServerRepositoryConfiguration().executeUpdate(QueryString);
	}

	public void clearProcessor() {
		String QueryString = "update " + this.getFrameworkExecution().getExecutionServerRepositoryConfiguration().getMetadataTableConfiguration().getPRC_CTL()
				+ " set request_id = -1 where prc_id = " + this.prc_id;
		this.getFrameworkExecution().getExecutionServerRepositoryConfiguration().executeUpdate(QueryString);
	}

	public void removeFromQueue() {
		String QueryString = "delete from " + this.getFrameworkExecution().getExecutionServerRepositoryConfiguration().getMetadataTableConfiguration().getPRC_REQ()
				+ " where request_id = " + this.que_id;
		this.getFrameworkExecution().getExecutionServerRepositoryConfiguration().executeUpdate(QueryString);
	}

	public void getFields() {
		String QueryString = "";
		CachedRowSet crs = null;
		QueryString = "select request_id, request_type, script_nm, env_nm, prc_id from "
				+ this.getFrameworkExecution().getExecutionServerRepositoryConfiguration().getMetadataTableConfiguration().getPRC_REQ() + " where request_id = "
				+ this.que_id;
		crs = this.getFrameworkExecution().getExecutionServerRepositoryConfiguration().executeQuery(QueryString);

		try {
			while (crs.next()) {
				this.request_type = crs.getString("REQUEST_TYPE");
				this.env_nm = crs.getString("ENV_NM");
				this.script_nm = crs.getString("SCRIPT_NM");
			}

			crs.close();
		} catch (SQLException e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
	}

	public void execute() {
		// Execution logic
		System.out.println("Execution logic here");
		this.removeFromQueue();
		this.clearProcessor();

	}

	// Getters and setters
	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

}
