package io.metadew.iesi.connection.http;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;

/**
 * Object used to manage the http response resulting from the http connection object.
 * 
 * @author peter.billen
 *
 */
public class HttpResponse {

	private CloseableHttpResponse response;
	private StatusLine statusLine;
	private HttpEntity entity;
	private String entityString;
	
	// Constructor
	public HttpResponse() {
		super();
	}

	// Getters and setters
	public StatusLine getStatusLine() {
		return statusLine;
	}

	public void setStatusLine(StatusLine statusLine) {
		this.statusLine = statusLine;
	}

	public HttpEntity getEntity() {
		return entity;
	}

	public void setEntity(HttpEntity entity) {
		this.entity = entity;
	}

	public CloseableHttpResponse getResponse() {
		return response;
	}

	public void setResponse(CloseableHttpResponse response) {
		this.response = response;
	}

	public String getEntityString() {
		return entityString;
	}

	public void setEntityString(String entityString) {
		this.entityString = entityString;
	}


}