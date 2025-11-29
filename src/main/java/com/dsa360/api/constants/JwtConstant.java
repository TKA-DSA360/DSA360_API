package com.dsa360.api.constants;

public enum JwtConstant {
	HEADER_STRING("Authorization"), 
	TOKEN_PREFIX("Bearer "),
	SIGNING_KEY("MyDSA360ApplicationSigningKEY702019272609876654321"), 
	AUTHORITIES_KEY("scopes"),
	ACCESS_TOKEN_VALIDITY_SECONDS(String.valueOf(5 * 60 * 1000)), // 15 * 60 * 1000 in milliseconds
	REFRESH_TOKEN_VALIDITY_MILLISECONDS(String.valueOf(60 * 60 * 1000)); 

	private final String value;

	JwtConstant(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
