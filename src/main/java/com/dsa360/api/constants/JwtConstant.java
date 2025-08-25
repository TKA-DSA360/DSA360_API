package com.dsa360.api.constants;

public enum JwtConstant {
	HEADER_STRING(
			"Authorization"), TOKEN_PREFIX("Bearer "),
	SIGNING_KEY("MyDSA360ApplicationSigningKEY702019272609876654321"), AUTHORITIES_KEY("scopes"),
	ACCESS_TOKEN_VALIDITY_SECONDS(String.valueOf(15 * 60 * 1000)); // 15 * 60 * 1000 in milliseconds

	private final String value;

	JwtConstant(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
