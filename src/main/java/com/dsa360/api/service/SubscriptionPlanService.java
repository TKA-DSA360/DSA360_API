package com.dsa360.api.service;

import com.dsa360.api.dto.SubscriptionPlanDTO;

import java.util.List;

public interface SubscriptionPlanService {
    SubscriptionPlanDTO createPlan(SubscriptionPlanDTO planDTO);
    List<SubscriptionPlanDTO> getAllPlans();
    SubscriptionPlanDTO getPlan(String planId);
    SubscriptionPlanDTO updatePlan(SubscriptionPlanDTO planDTO);
    void deletePlan(String planId);
}