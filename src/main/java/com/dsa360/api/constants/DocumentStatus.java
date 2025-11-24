package com.dsa360.api.constants;

public enum DocumentStatus {
    UPLOADED("Uploaded"),
    VERIFIED("Verified"),
    REJECTED("Rejected"),
    PENDING("Pending");

    private final String value;

    DocumentStatus(String value) {
        this.value = value;
    }

	public String getValue() {
		return value;
	}

    
}
