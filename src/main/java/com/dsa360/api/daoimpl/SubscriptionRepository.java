package com.dsa360.api.daoimpl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.dsa360.api.entity.master.SubscriptionEntity;

@Repository
public class SubscriptionRepository {

    @Autowired
    @Qualifier("tenantSessionFactory")
    private SessionFactory sessionFactory;

    public void save(SubscriptionEntity subscription) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.saveOrUpdate(subscription);
        }
    }

    public SubscriptionEntity findById(String subscriptionId) {
        try (Session session = sessionFactory.getCurrentSession()) {
            return session.get(SubscriptionEntity.class, subscriptionId);
        }
    }

    public List<SubscriptionEntity> findByTenantId(String tenantId) {
        try (Session session = sessionFactory.getCurrentSession()) {
            return session.createQuery("FROM SubscriptionEntity WHERE tenantId = :tenantId", SubscriptionEntity.class)
                    .setParameter("tenantId", tenantId)
                    .getResultList();
        }
    }
}