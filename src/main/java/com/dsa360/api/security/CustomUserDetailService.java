
package com.dsa360.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.dsa360.api.constants.UserStatus;
import com.dsa360.api.exceptions.ResourceNotFoundException;
import com.dsa360.api.exceptions.UserDeactivatedException;
import com.dsa360.api.exceptions.UserSuspendedException;
import com.dsa360.api.service.SystemUserService;

/**
 * @author RAM
 *
 */
@Service
public class CustomUserDetailService implements UserDetailsService {

	@Autowired
	private SystemUserService service;

	@Override
	public UserDetails loadUserByUsername(String username) {
		System.out.println("in load user method");
		UserDetails user = null;
		try {
			user = service.loadUserByUserId(username);

		} catch (UserDeactivatedException e) {
			throw new UserDeactivatedException(UserStatus.DEACTIVATED);
		} catch (UserSuspendedException e) {
			throw new UserSuspendedException(UserStatus.SUSPENDED);
		}

		catch (Exception e) {
			e.printStackTrace();
			throw new ResourceNotFoundException("Invalid User Name !!");
		}

		return user;
	}

}
