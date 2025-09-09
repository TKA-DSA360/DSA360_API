package com.dsa360.api.daoimpl;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import com.dsa360.api.dao.SubscriptionPlanDAO;
import com.dsa360.api.entity.master.SubscriptionPlanEntity;
import com.dsa360.api.exceptions.ResourceNotFoundException;
import com.dsa360.api.exceptions.SomethingWentWrongException;

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
		try (Session session = sessionFactory.openSession()) {
			return session.createQuery("FROM SubscriptionPlanEntity", SubscriptionPlanEntity.class).getResultList();
		}
	}

	@Override
	public Optional<SubscriptionPlanEntity> findById(String planId) {
		try (Session session = sessionFactory.openSession()) {
			return Optional.ofNullable(session.get(SubscriptionPlanEntity.class, planId));
		}
	}

	@Override
	public void update(SubscriptionPlanEntity entity) {
		try (Session session = sessionFactory.openSession()) {
			Transaction transaction = session.beginTransaction();
			session.update(entity);
			transaction.commit();
		} catch (Exception e) {
			throw new SomethingWentWrongException("Failed to update plan: " + e.getMessage());
		}
	}

	@Override
	public void delete(String planId) {
		try (Session session = sessionFactory.openSession()) {
			Transaction transaction = session.beginTransaction();
			SubscriptionPlanEntity entity = session.get(SubscriptionPlanEntity.class, planId);
			if (entity != null) {
				session.delete(entity);
				transaction.commit();
			} else {
				throw new ResourceNotFoundException("Plan not found to delete : " + planId);
			}
		} catch (Exception e) {
			if (e.getCause() instanceof ConstraintViolationException
					|| e.getCause() instanceof SQLIntegrityConstraintViolationException
					|| e instanceof DataIntegrityViolationException) {
				throw new SomethingWentWrongException(
						"Cannot delete plan as it is referenced by other records: " + planId);
			}
		}

	}
}