package io.metadew.iesi.metadata.definition;

import java.util.List;

public class Environment {
	
	private String name;
	private String description;
	private List<EnvironmentParameter> parameters;
	
	//Constructors
	public Environment() {
		
	}

	//Getters and Setters
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

	public List<EnvironmentParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<EnvironmentParameter> parameters) {
		this.parameters = parameters;
	}
		
	
}