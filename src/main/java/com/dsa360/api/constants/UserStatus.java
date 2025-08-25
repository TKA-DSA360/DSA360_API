package com.dsa360.api.constants;

public enum UserStatus {
	DEACTIVATED("Deactivated"), 
	SUSPENDED("Suspended"), 
	ACTIVE("Active");

	private final String value;

	UserStatus(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
