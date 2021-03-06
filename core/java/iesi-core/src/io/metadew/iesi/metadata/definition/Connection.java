package io.metadew.iesi.metadata.definition;

import java.util.List;

public class Connection {

	private String name;
	private String type;
	private String description;
	private String environment;
	private List<ConnectionParameter> parameters;

	// Constructors
	public Connection() {

	}

	// Getters and Setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}
	
	public List<ConnectionParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<ConnectionParameter> parameters) {
		this.parameters = parameters;
	}


}