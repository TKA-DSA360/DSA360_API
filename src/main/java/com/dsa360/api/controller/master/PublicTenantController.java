package com.dsa360.api.controller.master;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dsa360.api.config.TenantContext;
import com.dsa360.api.dto.SubscriptionDTO;
import com.dsa360.api.exceptions.SomethingWentWrongException;
import com.dsa360.api.service.SubscriptionService;
import com.dsa360.api.service.TenantService;

@RestController
@RequestMapping("/public/tenants")
public class PublicTenantController {

	@Autowired
	private TenantService tenantService;
	@Autowired
	private SubscriptionService subscriptionService;

	@PostMapping("/register")
	public ResponseEntity<String> registerTenant(@RequestParam String tenantName,@RequestParam String email) {
		TenantContext.setCurrentTenant("master");
		String tenantId = tenantService.createTenant(tenantName,email);
		return ResponseEntity.ok("Tenant registered with ID: " + tenantId);
	}

	@PostMapping("/subscriptions")
	public ResponseEntity<SubscriptionDTO> createSubscription(@RequestBody @Valid SubscriptionDTO subscriptionDTO) {
		try {
			TenantContext.setCurrentTenant("master");
			SubscriptionDTO createdSubscription = subscriptionService.createSubscription(subscriptionDTO);
			return new ResponseEntity<>(createdSubscription, HttpStatus.CREATED);
		} finally {
			TenantContext.clear();
		}
	}

	@PutMapping("/{subscriptionId}/change-plan")
	public ResponseEntity<SubscriptionDTO> chnageSubscriptionPlan(@PathVariable String subscriptionId,
			@RequestParam String newPlanId) {

		TenantContext.setCurrentTenant("master");
		try {
			SubscriptionDTO updatedSubscription = subscriptionService.changeSubscriptionPlan(subscriptionId, newPlanId);
			System.out.println("Updated Subscriptions: " + updatedSubscription);
			return new ResponseEntity<SubscriptionDTO>(updatedSubscription, HttpStatus.OK);
		} finally {
			TenantContext.clear();
		}
	}

}
