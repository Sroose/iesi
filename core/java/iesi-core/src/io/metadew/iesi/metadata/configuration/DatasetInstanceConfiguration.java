package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.Dataset;
import io.metadew.iesi.metadata.definition.DatasetInstance;
import io.metadew.iesi.metadata.definition.DatasetInstanceLabel;
import io.metadew.iesi.metadata.definition.DatasetInstanceParameter;

public class DatasetInstanceConfiguration {

	private DatasetInstance datasetInstance;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public DatasetInstanceConfiguration(DatasetInstance datasetInstance, FrameworkExecution frameworkExecution) {
		this.setDatasetInstance(datasetInstance);
		this.setFrameworkExecution(frameworkExecution);
	}

	public DatasetInstanceConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	// Insert
	public String getInsertStatement(String datasetName) {
		String sql = "";

		sql += "INSERT INTO "
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
						.getMetadataTableConfiguration().getTableName("DatasetInstances");
		sql += " (DST_ID, DST_INST_ID, DST_INST_NM, DST_INST_DSC) ";
		sql += "VALUES ";
		sql += "(";
		sql += "(" + SQLTools.GetLookupIdStatement(
				this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
						.getMetadataTableConfiguration().getTableName("Datasets"),
				"DST_ID", "where DST_NM = '" + datasetName) + "')";
		sql += ",";
		sql += "(" + SQLTools.GetNextIdStatement(
				this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
						.getMetadataTableConfiguration().getTableName("DatasetInstances"),
				"DST_INST_ID") + ")";
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getDatasetInstance().getName());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getDatasetInstance().getDescription());
		sql += ")";
		sql += ";";

		// add Parameters
		String sqlParameters = this.getParameterInsertStatements(datasetName);
		if (!sqlParameters.equals("")) {
			sql += "\n";
			sql += sqlParameters;
		}

		// add Lables
		String sqlLabels = this.getLabelInsertStatements(datasetName);
		if (!sqlLabels.equals("")) {
			sql += "\n";
			sql += sqlLabels;
		}

		return sql;
	}

	private String getParameterInsertStatements(String datasetName) {
		String result = "";

		// Catch null parameters
		if (this.getDatasetInstance().getParameters() == null)
			return result;

		for (DatasetInstanceParameter datasetInstanceParameter : this.getDatasetInstance().getParameters()) {
			DatasetInstanceParameterConfiguration datasetInstanceParameterConfiguration = new DatasetInstanceParameterConfiguration(
					datasetInstanceParameter, this.getFrameworkExecution());
			if (!result.equals(""))
				result += "\n";
			result += datasetInstanceParameterConfiguration.getInsertStatement(datasetName,
					this.getDatasetInstance().getName());
		}

		return result;
	}

	private String getLabelInsertStatements(String datasetName) {
		String result = "";

		// Catch null labels
		if (this.getDatasetInstance().getLabels() == null)
			return result;

		for (DatasetInstanceLabel datasetInstanceLabel : this.getDatasetInstance().getLabels()) {
			DatasetInstanceLabelConfiguration datasetInstanceLabelConfiguration = new DatasetInstanceLabelConfiguration(
					datasetInstanceLabel, this.getFrameworkExecution());
			if (!result.equals(""))
				result += "\n";
			result += datasetInstanceLabelConfiguration.getInsertStatement(datasetName,
					this.getDatasetInstance().getName());
		}

		return result;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DatasetInstance getDatasetInstance(long datasetId, String datasetInstanceName) {
		DatasetInstance datasetInstance = new DatasetInstance();
		CachedRowSet crsDatasetInstance = null;
		String queryDatasetInstance = "select DST_ID, DST_INST_ID, DST_INST_NM, DST_INST_DSC from "
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
						.getMetadataTableConfiguration().getTableName("DatasetInstances")
				+ " where DST_ID = " + datasetId + " and DST_INST_NM = '" + datasetInstanceName + "'";
		crsDatasetInstance = this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
				.executeQuery(queryDatasetInstance);
		DatasetInstanceParameterConfiguration datasetInstanceParameterConfiguration = new DatasetInstanceParameterConfiguration(
				this.getFrameworkExecution());
		DatasetInstanceLabelConfiguration datasetInstanceLabelConfiguration = new DatasetInstanceLabelConfiguration(
				this.getFrameworkExecution());
		try {
			while (crsDatasetInstance.next()) {
				datasetInstance.setName(datasetInstanceName);
				datasetInstance.setId(crsDatasetInstance.getLong("DST_INST_ID"));
				datasetInstance.setDescription(crsDatasetInstance.getString("DST_INST_DSC"));

				// Get parameters
				CachedRowSet crsDatasetInstanceParameters = null;
				String queryDatasetInstanceParameters = "select DST_ID, DST_INST_ID, DST_INST_PAR_NM, DST_INST_PAR_VAL from "
						+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
								.getMetadataTableConfiguration().getTableName("DatasetInstanceParameters")
						+ " where DST_ID = " + datasetId + " and DST_INST_ID = " + datasetInstance.getId();
				crsDatasetInstanceParameters = this.getFrameworkExecution().getMetadataControl()
						.getConnectivityRepositoryConfiguration().executeQuery(queryDatasetInstanceParameters);
				List<DatasetInstanceParameter> datasetInstanceParameterList = new ArrayList();
				while (crsDatasetInstanceParameters.next()) {
					datasetInstanceParameterList.add(datasetInstanceParameterConfiguration.getDatasetInstanceParameter(
							datasetId, datasetInstance.getId(),
							crsDatasetInstanceParameters.getString("DST_INST_PAR_NM")));
				}
				datasetInstance.setParameters(datasetInstanceParameterList);
				crsDatasetInstanceParameters.close();

				// Get labels
				CachedRowSet crsDatasetInstanceLabels = null;
				String queryDatasetInstanceLabels = "select DST_ID, DST_INST_ID, DST_INST_LBL_VAL from "
						+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
								.getMetadataTableConfiguration().getTableName("DatasetInstanceLabels")
						+ " where DST_ID = " + datasetId + " and DST_INST_ID = " + datasetInstance.getId();
				crsDatasetInstanceLabels = this.getFrameworkExecution().getMetadataControl()
						.getConnectivityRepositoryConfiguration().executeQuery(queryDatasetInstanceLabels);
				List<DatasetInstanceLabel> datasetInstanceLabelList = new ArrayList();
				while (crsDatasetInstanceLabels.next()) {
					datasetInstanceLabelList.add(datasetInstanceLabelConfiguration.getDatasetInstanceLabel(datasetId,
							datasetInstance.getId(), crsDatasetInstanceLabels.getString("DST_INST_LBL_VAL")));
				}
				datasetInstance.setLabels(datasetInstanceLabelList);
				crsDatasetInstanceLabels.close();
			}
			crsDatasetInstance.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		return datasetInstance;
	}

	public DatasetInstance getDatasetInstance(String datasetName, String datasetInstanceName) {
		DatasetConfiguration datasetConfiguration = new DatasetConfiguration(this.getFrameworkExecution());
		return this.getDatasetInstance(datasetConfiguration.getDatasetId(datasetName), datasetInstanceName);
	}

	public DatasetInstance getDatasetInstance(Dataset dataset, String datasetInstanceName) {
		DatasetInstance datasetInstanceResult = null;
		for (DatasetInstance datasetInstance : dataset.getInstances()) {
			if (datasetInstance.getName().equalsIgnoreCase(datasetInstanceName)) {
				datasetInstanceResult = datasetInstance;
				break;
			}
		}

		return datasetInstanceResult;
	}

	// Getters and Setters
	public DatasetInstance getDatasetInstance() {
		return datasetInstance;
	}

	public void setDatasetInstance(DatasetInstance datasetInstance) {
		this.datasetInstance = datasetInstance;
	}

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

}