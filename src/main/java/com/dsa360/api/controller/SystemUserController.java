package com.dsa360.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dsa360.api.entity.SystemUserEntity;
import com.dsa360.api.service.SystemUserService;

@RestController
@RequestMapping("/system-user")

public class SystemUserController {

	@Autowired
	private SystemUserService userService;

	@GetMapping("/get-user-by-username/{username}")
	@PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_SUB_ADMIN') OR hasRole('ROLE_AGENT')")
	public ResponseEntity<SystemUserEntity> getSystemUser(@PathVariable String username) {
		SystemUserEntity user = userService.getSystemUserByUsername(username);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	@GetMapping("/get-all-user")
	@PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_SUB_ADMIN')")
	public ResponseEntity<List<SystemUserEntity>> getAllSystemUser() {
		List<SystemUserEntity> allSystemUser = userService.getAllSystemUser();
		return new ResponseEntity<>(allSystemUser, HttpStatus.OK);
	}

}