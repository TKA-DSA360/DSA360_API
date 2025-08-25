package com.dsa360.api.serviceimpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ValidationException;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsa360.api.constants.TrancheStatus;
import com.dsa360.api.dao.LoanDisbursementDao;
import com.dsa360.api.dto.loan.DisbursementRequestDTO;
import com.dsa360.api.dto.loan.DisbursementResponseDTO;
import com.dsa360.api.dto.loan.RepaymentRequestDTO;
import com.dsa360.api.dto.loan.RepaymentResponseDTO;
import com.dsa360.api.dto.loan.TrancheRequestDTO;
import com.dsa360.api.dto.loan.TrancheResponseDTO;
import com.dsa360.api.entity.loan.DisbursementEntity;
import com.dsa360.api.entity.loan.LoanApplicationEntity;
import com.dsa360.api.entity.loan.LoanDisbursementEntity;
import com.dsa360.api.entity.loan.ReconciliationEntity;
import com.dsa360.api.entity.loan.RepaymentEntity;
import com.dsa360.api.entity.loan.TrancheAuditEntity;
import com.dsa360.api.entity.loan.TrancheEntity;
import com.dsa360.api.exceptions.ResourceNotFoundException;
import com.dsa360.api.exceptions.SomethingWentWrongException;
import com.dsa360.api.service.LoanDisbursementService;


@Service
public class LoanDisbursementServiceImpl implements LoanDisbursementService {

    private final LoanDisbursementDao dao;
    private final SessionFactory sessionFactory;

    public LoanDisbursementServiceImpl(LoanDisbursementDao dao, SessionFactory sessionFactory) {
        this.dao = dao;
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional
    public DisbursementResponseDTO createDisbursement(String loanApplicationId, DisbursementRequestDTO request) {
        if (request == null) throw new ValidationException("Disbursement request required");

        LoanApplicationEntity loan = sessionFactory.getCurrentSession().get(LoanApplicationEntity.class, loanApplicationId);
        if (loan == null) throw new ResourceNotFoundException("Loan not found: " + loanApplicationId);

        DisbursementEntity disb = new DisbursementEntity();
        disb.setLoanApplication(loan);
        disb.setSanctionedAmount(request.getSanctionedAmount());
        disb.setFees(request.getFees());
        disb.setNetDisbursedAmount(request.getNetDisbursedAmount());
        disb.setDisbursementDate(LocalDate.now());
        dao.saveDisbursement(disb);

        // ensure summary exists
        LoanDisbursementEntity summary = dao.findLoanDisbursementByLoanId(loanApplicationId);
        if (summary == null) {
            summary = new LoanDisbursementEntity();
            summary.setLoanApplication(loan);
            summary.setTotalDisbursed(0.0);
            dao.saveLoanDisbursement(summary);
        }

        // audit
        saveAudit(disb, "CREATED", "system", "Disbursement created");

        return toDisbursementResponse(disb);
    }

    @Override
    @Transactional
    public TrancheResponseDTO addTranche(String disbursementId, TrancheRequestDTO request) {
        if (request == null) throw new ValidationException("Tranche request required");
        if (request.getTrancheAmount() == null || request.getTrancheAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new ValidationException("Tranche amount must be positive");

        DisbursementEntity disb = dao.findDisbursementById(disbursementId);
        if (disb == null) throw new ResourceNotFoundException("Disbursement not found: " + disbursementId);

        // idempotency: if key provided, return existing
        if (request.getIdempotencyKey() != null) {
            TrancheEntity existing = dao.findByDisbursementIdAndIdempotencyKey(disbursementId, request.getIdempotencyKey());
            if (existing != null) return toTrancheResponse(existing);
        }

        // validate sum of tranches <= netDisbursed
        BigDecimal existingSum = BigDecimal.ZERO;
        if (disb.getTranches() != null) {
            for (TrancheEntity t : disb.getTranches()) {
                existingSum = existingSum.add(t.getTrancheAmount() != null ? t.getTrancheAmount() : BigDecimal.ZERO);
            }
        }
        BigDecimal allowed = disb.getNetDisbursedAmount() != null ? disb.getNetDisbursedAmount().subtract(existingSum) : BigDecimal.ZERO;
        if (request.getTrancheAmount().compareTo(allowed) > 0) {
            throw new SomethingWentWrongException("Tranche exceeds available disbursement. Allowed: " + allowed);
        }

        TrancheEntity tranche = new TrancheEntity();
        tranche.setDisbursement(disb);
        tranche.setTrancheAmount(request.getTrancheAmount());
        tranche.setTrancheDate(request.getTrancheDate() != null ? request.getTrancheDate() : LocalDate.now());
        tranche.setStatus(TrancheStatus.PENDING);
        tranche.setInitiatedBy(request.getInitiatedBy());
        tranche.setInitiatedAt(LocalDateTime.now());
        tranche.setIdempotencyKey(request.getIdempotencyKey());

        dao.saveTranche(tranche);

        saveAudit(tranche, "TRANCHE_CREATED", request.getInitiatedBy(), "Tranche created with idempotencyKey:" + request.getIdempotencyKey());

        return toTrancheResponse(tranche);
    }

    @Override
    @Transactional
    public TrancheResponseDTO initiateTranche(String trancheId, String externalTxnId, String initiatedBy) {
        TrancheEntity tranche = dao.findTrancheById(trancheId);
        if (tranche == null) throw new ResourceNotFoundException("Tranche not found: " + trancheId);

        if (tranche.getStatus() == TrancheStatus.SUCCESS) throw new SomethingWentWrongException("Tranche already successful");
        tranche.setExternalTransactionId(externalTxnId);
        tranche.setStatus(TrancheStatus.INITIATED);
        tranche.setInitiatedBy(initiatedBy);
        tranche.setInitiatedAt(LocalDateTime.now());

        dao.saveTranche(tranche);
        saveAudit(tranche, "TRANCHE_INITIATED", initiatedBy, "ExternalTxn:" + externalTxnId);
        return toTrancheResponse(tranche);
    }

    @Override
    @Transactional
    public TrancheResponseDTO confirmTranche(String trancheId, String confirmedBy) {
        TrancheEntity tranche = dao.findTrancheById(trancheId);
        if (tranche == null) throw new ResourceNotFoundException("Tranche not found: " + trancheId);

        if (!(tranche.getStatus() == TrancheStatus.PENDING || tranche.getStatus() == TrancheStatus.INITIATED)) {
            throw new SomethingWentWrongException("Tranche not in state to confirm: " + tranche.getStatus());
        }

        tranche.setStatus(TrancheStatus.SUCCESS);
        tranche.setConfirmedBy(confirmedBy);
        tranche.setConfirmedAt(LocalDateTime.now());
        dao.saveTranche(tranche);

        // update aggregate summary (optimistic lock safe)
        DisbursementEntity disb = tranche.getDisbursement();
        LoanApplicationEntity loan = disb.getLoanApplication();
        LoanDisbursementEntity summary = dao.findLoanDisbursementByLoanId(loan.getId());
        if (summary == null) {
            summary = new LoanDisbursementEntity();
            summary.setLoanApplication(loan);
            summary.setTotalDisbursed(0.0);
        }
        double add = tranche.getTrancheAmount().doubleValue();
        summary.setTotalDisbursed((summary.getTotalDisbursed() == null ? 0.0 : summary.getTotalDisbursed()) + add);
        dao.saveLoanDisbursement(summary);

        saveAudit(tranche, "TRANCHE_CONFIRMED", confirmedBy, "Confirmed and summary updated");

        return toTrancheResponse(tranche);
    }

    @Override
    @Transactional
    public RepaymentResponseDTO makeRepayment(String loanApplicationId, RepaymentRequestDTO request) {
        if (request == null) throw new ValidationException("Repayment request required");

        LoanApplicationEntity loan = sessionFactory.getCurrentSession().get(LoanApplicationEntity.class, loanApplicationId);
        if (loan == null) throw new ResourceNotFoundException("Loan not found: " + loanApplicationId);

        DisbursementEntity disb = dao.findDisbursementById(request.getDisbursementId());
        if (disb == null) throw new ResourceNotFoundException("Disbursement not found: " + request.getDisbursementId());

        if (request.getRepaymentAmount() == null || request.getRepaymentAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new ValidationException("Repayment amount must be positive");

        RepaymentEntity repayment = new RepaymentEntity();
        repayment.setDisbursement(disb);
        repayment.setRepaymentAmount(request.getRepaymentAmount());
        repayment.setRepaymentDate(request.getRepaymentDate() != null ? request.getRepaymentDate() : LocalDate.now());
        repayment.setPaidBy(request.getPaidBy());

        dao.saveRepayment(repayment);
        // you may want to update outstanding balance here

        return toRepaymentResponse(repayment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DisbursementResponseDTO> getDisbursementsByLoan(String loanApplicationId) {
        return dao.findDisbursementsByLoanId(loanApplicationId).stream().map(this::toDisbursementResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrancheResponseDTO> getTranchesByDisbursement(String disbursementId) {
        return dao.findTranchesByDisbursementId(disbursementId).stream().map(this::toTrancheResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepaymentResponseDTO> getRepaymentsByLoan(String loanApplicationId) {
        return dao.findRepaymentsByLoanId(loanApplicationId).stream().map(this::toRepaymentResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void reconcileExternalTransaction(String externalTxnId, String status, BigDecimal amount, LocalDate date) {
        // attempt to find tranche by externalTxnId
        String hql = "from TrancheEntity t where t.externalTransactionId = :ext";
        TrancheEntity t = (TrancheEntity) sessionFactory.getCurrentSession()
                .createQuery(hql, TrancheEntity.class).setParameter("ext", externalTxnId).uniqueResult();

        ReconciliationEntity recon = new ReconciliationEntity();
        recon.setExternalTxnId(externalTxnId);
        recon.setExternalAmount(amount);
        recon.setExternalDate(date);
        recon.setReconciledAt(LocalDateTime.now());

        if (t == null) {
            recon.setStatus("PENDING");
            recon.setRemarks("No matching tranche found");
            dao.saveReconciliation(recon);
            return;
        }

        // If found, validate amounts
        if (t.getTrancheAmount().compareTo(amount) == 0 && "SUCCESS".equalsIgnoreCase(status)) {
            t.setStatus(TrancheStatus.SUCCESS);
            t.setConfirmedAt(LocalDateTime.now());
            dao.saveTranche(t);

            // update summary
            LoanDisbursementEntity summary = dao.findLoanDisbursementByLoanId(t.getDisbursement().getLoanApplication().getId());
            if (summary == null) {
                summary = new LoanDisbursementEntity();
                summary.setLoanApplication(t.getDisbursement().getLoanApplication());
                summary.setTotalDisbursed(0.0);
            }
            summary.setTotalDisbursed((summary.getTotalDisbursed()==null?0.0:summary.getTotalDisbursed()) + t.getTrancheAmount().doubleValue());
            dao.saveLoanDisbursement(summary);

            recon.setStatus("MATCHED");
            recon.setTrancheId(t.getId());
            recon.setRemarks("Matched and marked success");
            dao.saveReconciliation(recon);
            saveAudit(t, "TRANCHE_RECONCILED", "system", "Reconciled via externalTxn");
        } else {
            recon.setStatus("MISMATCH");
            recon.setTrancheId(t.getId());
            recon.setRemarks("Amount or status mismatch");
            dao.saveReconciliation(recon);
            saveAudit(t, "TRANCHE_MISMATCH", "system", "Mismatch on reconciliation");
        }
    }

    /* ---------- helpers ---------- */

    private void saveAudit(Object obj, String event, String by, String data) {
        TrancheAuditEntity audit = new TrancheAuditEntity();
        if (obj instanceof DisbursementEntity) {
            DisbursementEntity d = (DisbursementEntity) obj;
            audit.setTrancheId(null);
            audit.setEventType(event);
            audit.setEventBy(by);
            audit.setEventAt(LocalDateTime.now());
            audit.setData("DisbursementId=" + d.getId() + " " + data);
        } else if (obj instanceof TrancheEntity) {
            TrancheEntity t = (TrancheEntity) obj;
            audit.setTrancheId(t.getId());
            audit.setEventType(event);
            audit.setEventBy(by);
            audit.setEventAt(LocalDateTime.now());
            audit.setData(data);
        } else {
            audit.setEventType(event);
            audit.setEventBy(by);
            audit.setEventAt(LocalDateTime.now());
            audit.setData(data);
        }
        dao.saveTrancheAudit(audit);
    }

    private DisbursementResponseDTO toDisbursementResponse(DisbursementEntity e) {
        DisbursementResponseDTO dto = new DisbursementResponseDTO();
        dto.setDisbursementId(e.getId());
        dto.setLoanId(e.getLoanApplication() != null ? e.getLoanApplication().getId() : null);
        dto.setSanctionedAmount(e.getSanctionedAmount());
        dto.setFees(e.getFees());
        dto.setNetDisbursedAmount(e.getNetDisbursedAmount());
        dto.setDisbursementDate(e.getDisbursementDate());
        if (e.getTranches() != null) {
            dto.setTranches(e.getTranches().stream().map(this::toTrancheResponse).collect(Collectors.toList()));
        }
        return dto;
    }

    private TrancheResponseDTO toTrancheResponse(TrancheEntity t) {
        TrancheResponseDTO dto = new TrancheResponseDTO();
        dto.setTrancheId(t.getId());
        dto.setDisbursementId(t.getDisbursement() != null ? t.getDisbursement().getId() : null);
        dto.setTrancheAmount(t.getTrancheAmount());
        dto.setTrancheDate(t.getTrancheDate());
        dto.setStatus(t.getStatus() != null ? t.getStatus().name() : null);
        dto.setExternalTransactionId(t.getExternalTransactionId());
        dto.setIdempotencyKey(t.getIdempotencyKey());
        return dto;
    }

    private RepaymentResponseDTO toRepaymentResponse(RepaymentEntity r) {
        RepaymentResponseDTO dto = new RepaymentResponseDTO();
        dto.setRepaymentId(r.getId());
        dto.setDisbursementId(r.getDisbursement() != null ? r.getDisbursement().getId() : null);
        dto.setRepaymentAmount(r.getRepaymentAmount());
        dto.setRepaymentDate(r.getRepaymentDate());
        return dto;
    }
}
