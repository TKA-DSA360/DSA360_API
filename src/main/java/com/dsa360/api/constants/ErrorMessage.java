package com.dsa360.api.constants;

public enum ErrorMessage {

	DELETE_FILE_ERROR("Something went wrong during delete the directory if it exists");

	private final String value;

	private ErrorMessage(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
