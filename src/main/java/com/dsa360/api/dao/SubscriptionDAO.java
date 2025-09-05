package com.dsa360.api.dao;

import java.util.List;
import java.util.Optional;

import com.dsa360.api.entity.master.SubscriptionEntity;

public interface SubscriptionDAO {
    void save(SubscriptionEntity entity);
    List<SubscriptionEntity> findByTenantId(String tenantId);
    Optional<SubscriptionEntity> findByIdAndTenantId(String subscriptionId, String tenantId);
    void update(SubscriptionEntity entity);
    void delete(String subscriptionId, String tenantId);
}