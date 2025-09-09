package com.dsa360.api.controller.master;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dsa360.api.config.TenantContext;
import com.dsa360.api.dto.SubscriptionDTO;
import com.dsa360.api.exceptions.SomethingWentWrongException;
import com.dsa360.api.service.SubscriptionService;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

   

    @PreAuthorize("hasRole('ROLE_MASTER')")
    @GetMapping("/tenant")
    public ResponseEntity<List<SubscriptionDTO>> getSubscriptionsByTenant(@RequestParam String tenantId) {
    	
       
        if (tenantId == null) {
            throw new SomethingWentWrongException("Tenant ID not set");
        }
        TenantContext.setCurrentTenant("master");
        try {
            return new ResponseEntity<>(subscriptionService.getSubscriptionsByTenant(tenantId), HttpStatus.OK);
        } finally {
            TenantContext.clear();
        }
    }

    @PreAuthorize("hasRole('ROLE_MASTER')")
    @GetMapping
    public ResponseEntity<SubscriptionDTO> getSubscription(@RequestParam String subscriptionId) {
        
        TenantContext.setCurrentTenant("master");
        try {
            return new ResponseEntity<>(subscriptionService.getSubscription(subscriptionId), HttpStatus.OK);
        } finally {
            TenantContext.clear();
        }
    }

   

   
}