package com.dsa360.api.dao;

import java.util.List;
import java.util.Optional;

import com.dsa360.api.entity.master.SubscriptionPlanEntity;

public interface SubscriptionPlanDAO {
    void save(SubscriptionPlanEntity entity);
    List<SubscriptionPlanEntity> findAll();
    Optional<SubscriptionPlanEntity> findById(String planId);
    void update(SubscriptionPlanEntity entity);
    void delete(String planId);
}