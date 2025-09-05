package com.dsa360.api.daoimpl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.dsa360.api.dao.TenantDao;
import com.dsa360.api.entity.master.TenantEntity;

@Repository
public class TenantDaoImpl implements TenantDao {

	@Autowired
	@Qualifier("masterSessionFactory")
	private SessionFactory sessionFactory;

	@Override
	@Transactional("masterTransactionManager")
	public TenantEntity findById(String tenantId) {
		try (Session session = sessionFactory.openSession()) {
			return session.get(TenantEntity.class, tenantId);
		}
	}

	@Override
	@Transactional("masterTransactionManager")
	public List<TenantEntity> findAll() {
		try (Session session = sessionFactory.openSession()) {
			return session.createQuery("FROM TenantEntity", TenantEntity.class).getResultList();
		}
	}

	@Override
	@Transactional("masterTransactionManager")
	public void save(TenantEntity tenant) {
		try (Session session = sessionFactory.openSession()) {
			Transaction transaction = session.beginTransaction();
			session.saveOrUpdate(tenant);
			transaction.commit();
		}
	}

	@Override
	@Transactional("masterTransactionManager")
	public void delete(String tenantId) {
		try (Session session = sessionFactory.openSession()) {
			TenantEntity tenant = session.get(TenantEntity.class, tenantId);
			if (tenant != null) {
				session.delete(tenant);
			}
		}
	}
}