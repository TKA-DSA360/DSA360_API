package com.dsa360.api.daoimpl;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.dsa360.api.dao.SubscriptionDAO;
import com.dsa360.api.entity.master.SubscriptionEntity;

@Repository
public class SubscriptionDAOImpl implements SubscriptionDAO {

    @Autowired
    @Qualifier("tenantSessionFactory")
    private SessionFactory sessionFactory;

    @Override
    public void save(SubscriptionEntity entity) {
        try (Session session = sessionFactory.openSession()) {
        	Transaction transaction = session.beginTransaction();
            session.save(entity);
            transaction.commit();   
        }
    }

    @Override
    public List<SubscriptionEntity> findByTenantId(String tenantId) {
        try (Session session = sessionFactory.getCurrentSession()) {
            return session.createQuery("FROM SubscriptionEntity WHERE tenantId = :tenantId", SubscriptionEntity.class)
                    .setParameter("tenantId", tenantId)
                    .getResultList();
        }
    }

    @Override
    public Optional<SubscriptionEntity> findByIdAndTenantId(String subscriptionId, String tenantId) {
        try (Session session = sessionFactory.getCurrentSession()) {
            return Optional.ofNullable(session.createQuery("FROM SubscriptionEntity WHERE subscriptionId = :subscriptionId AND tenantId = :tenantId", SubscriptionEntity.class)
                    .setParameter("subscriptionId", subscriptionId)
                    .setParameter("tenantId", tenantId)
                    .uniqueResult());
        }
    }

    @Override
    public void update(SubscriptionEntity entity) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.update(entity);
        }
    }

    @Override
    public void delete(String subscriptionId, String tenantId) {
        try (Session session = sessionFactory.getCurrentSession()) {
            SubscriptionEntity entity = session.createQuery("FROM SubscriptionEntity WHERE subscriptionId = :subscriptionId AND tenantId = :tenantId", SubscriptionEntity.class)
                    .setParameter("subscriptionId", subscriptionId)
                    .setParameter("tenantId", tenantId)
                    .uniqueResult();
            if (entity != null) {
                session.delete(entity);
            }
        }
    }
}