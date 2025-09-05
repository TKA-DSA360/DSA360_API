package com.dsa360.api.controller.master;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dsa360.api.service.TenantService;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {

	@Autowired
	private TenantService tenantService;

	@PreAuthorize("hasRole('ROLE_MASTER')")
	@PostMapping("/register")
	public ResponseEntity<String> registerTenant(@RequestParam String tenantName) {
		String tenantId = tenantService.createTenant(tenantName);
		return ResponseEntity.ok("Tenant registered with ID: " + tenantId);
	}
}