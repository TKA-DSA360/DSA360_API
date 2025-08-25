package com.dsa360.api.controller;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dsa360.api.aspect.TrackExecutionTime;
import com.dsa360.api.dto.LogedInUserDetailModelDto;
import com.dsa360.api.security.CustomUserDetail;
import com.dsa360.api.security.CustomUserDetailService;
import com.dsa360.api.service.SystemUserService;
import com.dsa360.api.utility.JwtUtil;

/**
 * @author RAM
 *
 */
@RestController
@RequestMapping("/auth")

public class AuthController {
	private static Logger log = LogManager.getLogger(AuthController.class);

	@Autowired
	SystemUserService userService;

	@Autowired
	CustomUserDetailService customUserDetailService;

	@Autowired
	JwtUtil jwtUtil;

	@Autowired
	private AuthenticationManager authenticationManager;

	// completed
	@PostMapping("/login-user")
	@TrackExecutionTime
	public ResponseEntity<LogedInUserDetailModelDto> login(@RequestParam String username, @RequestParam String password,
			HttpServletResponse response) throws AuthenticationException {

		log.info("Trying to login = {}", username);

		final var logedInUser = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(username, password));

		SecurityContextHolder.getContext().setAuthentication(logedInUser); // check

		CustomUserDetail userDetail = (CustomUserDetail) logedInUser.getPrincipal();
		Collection<? extends GrantedAuthority> authorities = userDetail.getAuthorities();

		List<String> roles = authorities.stream().map(authority -> authority.getAuthority().substring(5))
				.collect(Collectors.toList());

		log.info("Logged In = {}", username);

		final String token = jwtUtil.generateToken(logedInUser);

		response.setHeader("token", token);

		var model = new LogedInUserDetailModelDto(userDetail.getId(), userDetail.getUsername(), roles,
				userDetail.getStatus(), token);

		return new ResponseEntity<>(model, HttpStatus.OK);
	}

}
