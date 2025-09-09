package com.dsa360.api.daoimpl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.dsa360.api.config.TenantContext;
import com.dsa360.api.dao.TenantDao;
import com.dsa360.api.entity.DsaApplicationEntity;
import com.dsa360.api.entity.RegionsEntity;
import com.dsa360.api.entity.RoleEntity;
import com.dsa360.api.entity.SystemUserEntity;
import com.dsa360.api.entity.master.TenantEntity;

@Repository
public class TenantDaoImpl implements TenantDao {

	@Autowired
	@Qualifier("masterSessionFactory")
	private SessionFactory masterSessionFactory;

	@Autowired
	@Qualifier("tenantSessionFactory")
	private SessionFactory tenantSessionFactory;

	@Override
	@Transactional("masterTransactionManager")
	public TenantEntity findById(String tenantId) {
		try (Session session = masterSessionFactory.openSession()) {
			return session.get(TenantEntity.class, tenantId);
		}
	}

	@Override
	@Transactional("masterTransactionManager")
	public List<TenantEntity> findAll() {
		try (Session session = masterSessionFactory.openSession()) {
			return session.createQuery("FROM TenantEntity", TenantEntity.class).getResultList();
		}
	}

	@Override
	@Transactional("masterTransactionManager")
	public void save(TenantEntity tenant) {
		try (Session session = masterSessionFactory.openSession()) {
			Transaction transaction = session.beginTransaction();
			session.saveOrUpdate(tenant);
			transaction.commit();
		}
	}

	@Override
	@Transactional("masterTransactionManager")
	public void delete(String tenantId) {
		try (Session session = masterSessionFactory.openSession()) {
			Transaction transaction = session.beginTransaction();
			TenantEntity tenant = session.get(TenantEntity.class, tenantId);
			if (tenant != null) {
				session.delete(tenant);
			}
			transaction.commit();
		}
	}

	@Transactional("tenantTransactionManager")
	public void saveTenantEntities(String tenantId, RoleEntity adminRole, RegionsEntity defaultRegion, SystemUserEntity adminUser) {
		
		try {
			// Save default role
			try (Session session = tenantSessionFactory.openSession()) {
				Transaction transaction = session.beginTransaction();
				session.save(adminRole);
				transaction.commit();
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to save RoleEntity for tenantId: " + tenantId, e);
			}

			// Save default region
			try (Session session = tenantSessionFactory.openSession()) {
				Transaction transaction = session.beginTransaction();
				session.save(defaultRegion);
				transaction.commit();
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to save RegionsEntity for tenantId: " + tenantId, e);
			}

			// Save default admin user and DSA application
			try (Session session = tenantSessionFactory.openSession()) {
				Transaction transaction = session.beginTransaction();
					session.save(adminUser);
				transaction.commit();
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to save SystemUserEntity or DsaApplicationEntity for tenantId: " + tenantId, e);
			}
		} finally {
			TenantContext.clear();
		}
	}

	@Override
	public void update(TenantEntity tenant) {
		try (Session session = masterSessionFactory.openSession()) {
			Transaction transaction = session.beginTransaction();
			session.update(tenant);
			transaction.commit();
		} finally {
			TenantContext.clear();
		}
		
	}
}