package com.dsa360.api.dao;

import java.util.List;

import com.dsa360.api.entity.loan.DisbursementEntity;
import com.dsa360.api.entity.loan.LoanDisbursementEntity;
import com.dsa360.api.entity.loan.ReconciliationEntity;
import com.dsa360.api.entity.loan.RepaymentEntity;
import com.dsa360.api.entity.loan.TrancheAuditEntity;
import com.dsa360.api.entity.loan.TrancheEntity;

public interface LoanDisbursementDao {
    DisbursementEntity saveDisbursement(DisbursementEntity disbursement);
    DisbursementEntity findDisbursementById(String id);
    List<DisbursementEntity> findDisbursementsByLoanId(String loanApplicationId);

    TrancheEntity saveTranche(TrancheEntity tranche);
    TrancheEntity findTrancheById(String id);
    TrancheEntity findByDisbursementIdAndIdempotencyKey(String disbursementId, String idempotencyKey);
    List<TrancheEntity> findTranchesByDisbursementId(String disbursementId);

    RepaymentEntity saveRepayment(RepaymentEntity repayment);
    List<RepaymentEntity> findRepaymentsByLoanId(String loanApplicationId);

    LoanDisbursementEntity findLoanDisbursementByLoanId(String loanApplicationId);
    LoanDisbursementEntity saveLoanDisbursement(LoanDisbursementEntity entity);

    void saveTrancheAudit(TrancheAuditEntity audit);
    void saveReconciliation(ReconciliationEntity recon);
}
