package com.dsa360.api.daoimpl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import com.dsa360.api.dao.LoanDisbursementDao;
import com.dsa360.api.entity.loan.DisbursementEntity;
import com.dsa360.api.entity.loan.LoanDisbursementEntity;
import com.dsa360.api.entity.loan.ReconciliationEntity;
import com.dsa360.api.entity.loan.RepaymentEntity;
import com.dsa360.api.entity.loan.TrancheAuditEntity;
import com.dsa360.api.entity.loan.TrancheEntity;



@Repository
public class LoanDisbursementDaoImpl implements LoanDisbursementDao {

    private final SessionFactory sessionFactory;

    public LoanDisbursementDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public DisbursementEntity saveDisbursement(DisbursementEntity disbursement) {
        currentSession().saveOrUpdate(disbursement);
        return disbursement;
    }

    @Override
    public DisbursementEntity findDisbursementById(String id) {
        return currentSession().get(DisbursementEntity.class, id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DisbursementEntity> findDisbursementsByLoanId(String loanApplicationId) {
        String hql = "from DisbursementEntity d where d.loanApplication.id = :loanId";
        return currentSession().createQuery(hql, DisbursementEntity.class)
                .setParameter("loanId", loanApplicationId).getResultList();
    }

    @Override
    public TrancheEntity saveTranche(TrancheEntity tranche) {
        currentSession().saveOrUpdate(tranche);
        return tranche;
    }

    @Override
    public TrancheEntity findTrancheById(String id) {
        return currentSession().get(TrancheEntity.class, id);
    }

    @Override
    public TrancheEntity findByDisbursementIdAndIdempotencyKey(String disbursementId, String idempotencyKey) {
        if (idempotencyKey == null) return null;
        String hql = "from TrancheEntity t where t.disbursement.id = :disbId and t.idempotencyKey = :key";
        return currentSession().createQuery(hql, TrancheEntity.class)
                .setParameter("disbId", disbursementId)
                .setParameter("key", idempotencyKey)
                .uniqueResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<TrancheEntity> findTranchesByDisbursementId(String disbursementId) {
        String hql = "from TrancheEntity t where t.disbursement.id = :disbId";
        return currentSession().createQuery(hql, TrancheEntity.class)
                .setParameter("disbId", disbursementId).getResultList();
    }

    @Override
    public RepaymentEntity saveRepayment(RepaymentEntity repayment) {
        currentSession().saveOrUpdate(repayment);
        return repayment;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<RepaymentEntity> findRepaymentsByLoanId(String loanApplicationId) {
        String hql = "from RepaymentEntity r where r.disbursement.loanApplication.id = :loanId";
        return currentSession().createQuery(hql, RepaymentEntity.class)
                .setParameter("loanId", loanApplicationId).getResultList();
    }

    @Override
    public LoanDisbursementEntity findLoanDisbursementByLoanId(String loanApplicationId) {
        String hql = "from LoanDisbursementEntity ld where ld.loanApplication.id = :loanId";
        return currentSession().createQuery(hql, LoanDisbursementEntity.class)
                .setParameter("loanId", loanApplicationId).uniqueResult();
    }

    @Override
    public LoanDisbursementEntity saveLoanDisbursement(LoanDisbursementEntity entity) {
        currentSession().saveOrUpdate(entity);
        return entity;
    }

    @Override
    public void saveTrancheAudit(TrancheAuditEntity audit) {
        currentSession().save(audit);
    }

    @Override
    public void saveReconciliation(ReconciliationEntity recon) {
        currentSession().saveOrUpdate(recon);
    }
}
