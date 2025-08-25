package com.dsa360.api.constants;

public enum UserRole {

	ADMIN("ROLE_ADMIN"), // 0
	SUB_ADMIN("ROLE_SUB_ADMIN"), // 1
	AGENT("ROLE_AGENT"), // 2
	CUSTOMER("ROLE_CUSTOMER"); // 3

	private final String value;

	UserRole(String value) {
	        this.value = value;
	    }

	public String getValue() {
		return value;
	}

}
