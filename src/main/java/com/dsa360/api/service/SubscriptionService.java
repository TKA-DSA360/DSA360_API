package com.dsa360.api.service;

import com.dsa360.api.dto.SubscriptionDTO;

import java.util.List;

public interface SubscriptionService {
    SubscriptionDTO createSubscription(SubscriptionDTO subscriptionDTO);
    List<SubscriptionDTO> getSubscriptionsByTenant(String tenantId);
    SubscriptionDTO getSubscription(String subscriptionId, String tenantId);
    SubscriptionDTO updateSubscription(SubscriptionDTO subscriptionDTO);
    void deleteSubscription(String subscriptionId, String tenantId);
}