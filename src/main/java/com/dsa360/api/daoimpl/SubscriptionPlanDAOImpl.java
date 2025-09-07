package com.dsa360.api.daoimpl;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.dsa360.api.dao.SubscriptionPlanDAO;
import com.dsa360.api.entity.master.SubscriptionPlanEntity;

@Repository
public class SubscriptionPlanDAOImpl implements SubscriptionPlanDAO {

	@Autowired
	@Qualifier("masterSessionFactory")
	private SessionFactory sessionFactory;

	@Override
	public void save(SubscriptionPlanEntity entity) {
		try (Session session = sessionFactory.openSession()) {
			Transaction transaction = session.beginTransaction();
			session.save(entity);
			transaction.commit();

		}
	}

	@Override
	public List<SubscriptionPlanEntity> findAll() {
		try (Session session = sessionFactory.getCurrentSession()) {
			return session.createQuery("FROM SubscriptionPlanEntity", SubscriptionPlanEntity.class).getResultList();
		}
	}

	@Override
	public Optional<SubscriptionPlanEntity> findById(String planId) {
		try (Session session = sessionFactory.getCurrentSession()) {
			return Optional.ofNullable(session.get(SubscriptionPlanEntity.class, planId));
		}
	}

	@Override
	public void update(SubscriptionPlanEntity entity) {
		try (Session session = sessionFactory.getCurrentSession()) {
			session.update(entity);
		}
	}

	@Override
	public void delete(String planId) {
		try (Session session = sessionFactory.getCurrentSession()) {
			SubscriptionPlanEntity entity = session.get(SubscriptionPlanEntity.class, planId);
			if (entity != null) {
				session.delete(entity);
			}
		}
	}
}