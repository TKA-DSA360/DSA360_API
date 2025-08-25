package com.dsa360.api.dto;
/**
 * @author RAM
 *
 */
public class ExceptionRespone {
	
	private String message;
	private String requestPath;
	private String timestamp;
	
	public ExceptionRespone() {
		// TODO Auto-generated constructor stub
	}

	public ExceptionRespone(String message, String requestPath, String timestamp) {
		super();
		this.message = message;
		this.requestPath = requestPath;
		this.timestamp = timestamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRequestPath() {
		return requestPath;
	}

	public void setRequestPath(String requestPath) {
		this.requestPath = requestPath;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	

}
