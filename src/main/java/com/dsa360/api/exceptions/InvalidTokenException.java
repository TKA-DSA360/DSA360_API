package com.dsa360.api.exceptions;

/**
 * @author RAM
 *
 */
public class InvalidTokenException extends RuntimeException {

	private static final long serialVersionUID = 1L;

public InvalidTokenException(String msg) {
	super(msg);
}
}
