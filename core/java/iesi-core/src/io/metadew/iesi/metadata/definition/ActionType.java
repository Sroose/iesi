package io.metadew.iesi.metadata.definition;

import java.util.List;

public class ActionType {
	
	private String name;
	private String description;
	private List<ActionTypeParameter> parameters;
	
	//Constructors
	public ActionType() {
		
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

	public List<ActionTypeParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<ActionTypeParameter> parameters) {
		this.parameters = parameters;
	}
	

}