package com.dsa360.api.controller.master;

import com.dsa360.api.config.TenantContext;
import com.dsa360.api.dto.SubscriptionPlanDTO;
import com.dsa360.api.service.SubscriptionPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
public class SubscriptionPlanController {

    @Autowired
    private SubscriptionPlanService planService;

    @PreAuthorize("hasRole('ROLE_MASTER')")
    @PostMapping
    public ResponseEntity<SubscriptionPlanDTO> createPlan(@RequestBody SubscriptionPlanDTO planDTO) {
        TenantContext.setCurrentTenant("master");
        try {
            SubscriptionPlanDTO createdPlan = planService.createPlan(planDTO);
            return new ResponseEntity<>(createdPlan, HttpStatus.CREATED);
        } finally {
            TenantContext.clear();
        }
    }

    @PreAuthorize("hasRole('ROLE_MASTER')")
    @GetMapping
    public ResponseEntity<List<SubscriptionPlanDTO>> getAllPlans() {
        TenantContext.setCurrentTenant("master");
        try {
            return new ResponseEntity<>(planService.getAllPlans(), HttpStatus.OK);
        } finally {
            TenantContext.clear();
        }
    }

    @PreAuthorize("hasRole('ROLE_MASTER')")
    @GetMapping("/{planId}")
    public ResponseEntity<SubscriptionPlanDTO> getPlan(@PathVariable String planId) {
        TenantContext.setCurrentTenant("master");
        try {
            return new ResponseEntity<>(planService.getPlan(planId), HttpStatus.OK);
        } finally {
            TenantContext.clear();
        }
    }

    @PreAuthorize("hasRole('ROLE_MASTER')")
    @PutMapping("/{planId}")
    public ResponseEntity<SubscriptionPlanDTO> updatePlan(@PathVariable String planId, @RequestBody SubscriptionPlanDTO planDTO) {
        TenantContext.setCurrentTenant("master");
        try {
            planDTO.setPlanId(planId);
            SubscriptionPlanDTO updatedPlan = planService.updatePlan(planDTO);
            return new ResponseEntity<>(updatedPlan, HttpStatus.OK);
        } finally {
            TenantContext.clear();
        }
    }

    @PreAuthorize("hasRole('ROLE_MASTER')")
    @DeleteMapping("/{planId}")
    public ResponseEntity<Void> deletePlan(@PathVariable String planId) {
        TenantContext.setCurrentTenant("master");
        try {
            planService.deletePlan(planId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            TenantContext.clear();
        }
    }
}