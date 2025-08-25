package com.dsa360.api.daoimpl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dsa360.api.constants.ApprovalStatus;
import com.dsa360.api.constants.UserStatus;
import com.dsa360.api.dao.DSAUserMetricsDao;
import com.dsa360.api.entity.DsaApplicationEntity;
import com.dsa360.api.entity.SystemUserEntity;

@Repository
@Transactional
public class DSAUserMetricsDaoImpl implements DSAUserMetricsDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public long getTotalSystemUsers() {
		try (Session session = sessionFactory.openSession()) {
			Criteria criteria = session.createCriteria(SystemUserEntity.class);
			criteria.setProjection(Projections.rowCount());
			return (Long) criteria.uniqueResult();
		}
	}

	@Override
	public long getActiveSystemUsers() {
		try (Session session = sessionFactory.openSession()) {
			Criteria criteria = session.createCriteria(SystemUserEntity.class);
			criteria.add(Restrictions.eq("status", UserStatus.ACTIVE.getValue()));
			criteria.setProjection(Projections.rowCount());
			return (Long) criteria.uniqueResult();
		}
	}

	@Override
	public long getDailyNewDsaRegistrations() {
		try (Session session = sessionFactory.openSession()) {
			LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
			Criteria criteria = session.createCriteria(DsaApplicationEntity.class);
			criteria.add(Restrictions.ge("createdAt", oneDayAgo));
			criteria.setProjection(Projections.rowCount());
			return (Long) criteria.uniqueResult();
		}
	}

	@Override
	public long getWeeklyNewDsaRegistrations() {
		try (Session session = sessionFactory.openSession()) {
			LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
			Criteria criteria = session.createCriteria(DsaApplicationEntity.class);
			criteria.add(Restrictions.ge("createdAt", sevenDaysAgo));
			criteria.setProjection(Projections.rowCount());
			return (Long) criteria.uniqueResult();
		}
	}

	@Override
	public long getMonthlyNewDsaRegistrations() {
		try (Session session = sessionFactory.openSession()) {
			LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
			Criteria criteria = session.createCriteria(DsaApplicationEntity.class);
			criteria.add(Restrictions.ge("createdAt", thirtyDaysAgo));
			criteria.setProjection(Projections.rowCount());
			return (Long) criteria.uniqueResult();
		}
	}

	@Override
	public long getPendingDsaRegistrations() {
		try (Session session = sessionFactory.openSession()) {
			Criteria criteria = session.createCriteria(DsaApplicationEntity.class);
			criteria.add(Restrictions.eq("approvalStatus", ApprovalStatus.PENDING.getValue()));
			criteria.setProjection(Projections.rowCount());
			return (Long) criteria.uniqueResult();
		}
	}

	@Override
	public long getDeactivedSystemUser() {
		try (Session session = sessionFactory.openSession()) {
			Criteria criteria = session.createCriteria(SystemUserEntity.class);
			criteria.add(Restrictions.eq("status", UserStatus.DEACTIVATED.getValue()));
			criteria.setProjection(Projections.rowCount());
			return (Long) criteria.uniqueResult();
		}
	}
}
