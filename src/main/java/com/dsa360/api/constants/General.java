package com.dsa360.api.constants;

public enum General {
	
	DATE_FORMAT("yyyy-MM-dd HH:mm:ss"),
	DSA_NAME("dsaName"),
	DSA_ID("dsaId"),
	REGISTERED_NAME("registeredName"),
	CONTACT_INFO("contactInfo");
	

	private final String value;

	private General(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
