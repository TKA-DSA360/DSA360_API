package com.dsa360.api.daoimpl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dsa360.api.dao.AuditLogDao;
import com.dsa360.api.entity.AuditLog;

@Repository
public class AuditLogDaoImpl implements AuditLogDao {

	@Autowired
	private SessionFactory sf;

	@Override
	public void save(AuditLog auditLog) {

		try (var session = sf.openSession()) {
			session.beginTransaction();
			session.save(auditLog);
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	@Override
	public List<AuditLog> findAll() {
		try (var session = sf.openSession()) {
			return session.createQuery("from AuditLog", AuditLog.class).getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return List.of(); 
		}
	}

}
