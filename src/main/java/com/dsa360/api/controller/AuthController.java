package com.dsa360.api.controller;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dsa360.api.aspect.TrackExecutionTime;
import com.dsa360.api.config.TenantContext;
import com.dsa360.api.constants.JwtConstant;
import com.dsa360.api.daoimpl.TenantDaoImpl;
import com.dsa360.api.dto.LogedInUserDetailModelDto;
import com.dsa360.api.dto.LoginRequestDto;
import com.dsa360.api.entity.master.TenantEntity;
import com.dsa360.api.exceptions.SomethingWentWrongException;
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
	private JwtUtil jwtUtil;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private TenantDaoImpl tenantDao;

	// completed
	@PostMapping("/login-user")
	@TrackExecutionTime
	public ResponseEntity<LogedInUserDetailModelDto> login(@RequestBody LoginRequestDto loginRequest,
			HttpServletResponse response) throws AuthenticationException {
		
		
		String username = loginRequest.getUsername();
		String password = loginRequest.getPassword();
		String tenantId = loginRequest.getTenantId();
		
		log.info("Trying to login = {}  = {}", loginRequest.getUsername(), loginRequest.getTenantId());

		String userType;
		if (tenantId == null) {
			// Master login
			userType = "master";
			System.out.println("Logging in as master user");
			TenantContext.setCurrentTenant("master");
		} else {
			// Tenant login
			userType = "tenant";
			System.out.println("Logging in as tenant user for tenantId: " + tenantId);
			TenantEntity tenant = tenantDao.findById(tenantId);
			if (tenant == null) {
				throw new SomethingWentWrongException("Invalid tenant ID");
			}
			if (!"ACTIVE".equals(tenant.getSubscriptionStatus())) {
				throw new SomethingWentWrongException("Tenant subscription is not active");
			}
			TenantContext.setCurrentTenant(tenantId);
		}

		try {
			final var logedInUser = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			

			
			
			SecurityContextHolder.getContext().setAuthentication(logedInUser);

			CustomUserDetail userDetail = (CustomUserDetail) logedInUser.getPrincipal();
			userDetail.setUserType(userType); // Set userType
			Collection<? extends GrantedAuthority> authorities = userDetail.getAuthorities();

			List<String> roles = authorities.stream().map(authority -> authority.getAuthority().substring(5))
					.collect(Collectors.toList());

			log.info("Logged In = {} as {}", username, userType);

			final String token = jwtUtil.generateToken(logedInUser, tenantId, userType, username); // Pass userType

			response.setHeader("token", token);

			var model = new LogedInUserDetailModelDto(userDetail.getId(), userDetail.getUsername(), roles,
					userDetail.getStatus(), token);

			return new ResponseEntity<>(model, HttpStatus.OK);
		} finally {
			TenantContext.clear();
		}
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout(HttpServletRequest request) {
		String header = request.getHeader(JwtConstant.HEADER_STRING.getValue());
		if (header == null || !header.startsWith(JwtConstant.TOKEN_PREFIX.getValue())) {
			log.warn("No bearer token found in logout request");
			throw new SomethingWentWrongException("Missing or invalid Authorization header");
		}

		String authToken = header.replace(JwtConstant.TOKEN_PREFIX.getValue(), "");
		String username;
		String tenantId;
		String userType;

		try {
			username = jwtUtil.getUsernameFromToken(authToken);
			tenantId = jwtUtil.getTenantIdFromToken(authToken);
			userType = jwtUtil.getUserTypeFromToken(authToken);
			log.debug("Logout request for username: {}, tenantId: {}, userType: {}", username, tenantId, userType);
		} catch (Exception e) {
			log.error("Failed to extract token details: {}", e.getMessage());
			throw new SomethingWentWrongException("Invalid token");
		}

		// Validate tenantId and userType
		if (tenantId == null && !"master".equals(userType)) {
			log.warn("Invalid token: missing tenantId for non-master user");
			throw new SomethingWentWrongException("Invalid token: missing tenantId");
		}

		// Validate against SecurityContextHolder
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !username.equals(authentication.getName())
				|| !(authentication.getPrincipal() instanceof CustomUserDetail)) {
			log.warn("No valid authentication found for username: {}", username);
			throw new SomethingWentWrongException("User not authenticated");
		}

		CustomUserDetail userDetails = (CustomUserDetail) authentication.getPrincipal();
		String actualTenantId = userDetails.getTenantId();
		String actualUserType = userDetails.getUserType();
		String expectedTenant = tenantId != null && !"master".equals(userType) ? tenantId : "master";
		String actualTenant = "master".equals(actualUserType) ? "master" : actualTenantId;

		if (!expectedTenant.equals(actualTenant)) {
			log.warn("Tenant mismatch for username: {}. Expected tenant: {}, actual tenant: {}", username,
					expectedTenant, actualTenant);
			throw new SomethingWentWrongException("Tenant mismatch");
		}

		// Clear SecurityContextHolder for the current thread
		SecurityContextHolder.clearContext();
		log.info("User {} logged out from tenant: {}, SecurityContextHolder cleared", username, expectedTenant);

		// Instruct client to discard the token
		return ResponseEntity.ok().header("Clear-Token", "true")
				.body("Logged out successfully. Please discard the JWT token.");
	}
}