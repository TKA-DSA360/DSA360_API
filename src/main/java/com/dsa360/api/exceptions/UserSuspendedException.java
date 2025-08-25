package com.dsa360.api.exceptions;

import com.dsa360.api.constants.UserStatus;

public class UserSuspendedException extends RuntimeException {
	
	/**
	 * @author RAM
	 *
	 */
	private static final long serialVersionUID = 1L;

	public UserSuspendedException(String value) {
		super(value);
	}
	
	public UserSuspendedException(UserStatus status) {
		super(status.getValue());
	}

}
