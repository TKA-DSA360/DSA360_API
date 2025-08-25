package com.dsa360.api.exceptions;

public class TokenExpirationException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public TokenExpirationException(String msg) {
		super(msg);
	}

}
