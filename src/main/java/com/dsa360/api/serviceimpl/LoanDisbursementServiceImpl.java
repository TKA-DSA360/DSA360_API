package com.dsa360.api.serviceimpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ValidationException;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final RoundingMode MONEY_ROUNDING = RoundingMode.HALF_UP;

    @Autowired
    private  LoanDisbursementDao dao;
    
    @Autowired
    @Qualifier("tenantSessionFactory")
    private  SessionFactory sessionFactory;

    

    /* ============================ Disbursement ============================ */

    @Override
    @Transactional
    public DisbursementResponseDTO createDisbursement(String loanApplicationId, DisbursementRequestDTO request) {
        if (request == null) throw new ValidationException("Disbursement request required");

        LoanApplicationEntity loan = sessionFactory.getCurrentSession()
                .get(LoanApplicationEntity.class, loanApplicationId);
        if (loan == null) throw new ResourceNotFoundException("Loan not found: " + loanApplicationId);

        if (request.getSanctionedAmount() == null || request.getSanctionedAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Sanctioned amount must be positive");
        }
        if (request.getFeesPercentage() == null
                || request.getFeesPercentage().compareTo(BigDecimal.ZERO) < 0
                || request.getFeesPercentage().compareTo(HUNDRED) > 0) {
            throw new ValidationException("Fees percentage must be between 0 and 100");
        }

        // Calculate fees and net amount (scale = 2)
        BigDecimal fees = request.getSanctionedAmount()
                .multiply(request.getFeesPercentage())
                .divide(HUNDRED, 2, MONEY_ROUNDING);

        BigDecimal netDisbursedAmount = request.getSanctionedAmount().subtract(fees).setScale(2, MONEY_ROUNDING);
        if (netDisbursedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Net disbursed amount must be > 0");
        }

        // Create disbursement
        DisbursementEntity disb = new DisbursementEntity();
        disb.setLoanApplication(loan);
        disb.setSanctionedAmount(request.getSanctionedAmount().setScale(2, MONEY_ROUNDING));
        disb.setFees(fees);
        disb.setNetDisbursedAmount(netDisbursedAmount);
        disb.setDisbursementDate(LocalDate.now());
        dao.saveDisbursement(disb);

        // Ensure summary exists for the loan (one row per loan)
        LoanDisbursementEntity summary = dao.findLoanDisbursementByLoanId(loanApplicationId);
        if (summary == null) {
            summary = new LoanDisbursementEntity();
            summary.setLoanApplication(loan);
            summary.setTotalDisbursed(BigDecimal.ZERO.setScale(2, MONEY_ROUNDING));
            dao.saveLoanDisbursement(summary);
        }

        // Audit
        saveAudit(disb, "CREATED", "system", "Disbursement created");

        return toDisbursementResponse(disb);
    }

    /* ============================== Tranches ============================== */

    @Override
    @Transactional
    public TrancheResponseDTO addTranche(String disbursementId, TrancheRequestDTO request) {
        if (request == null) throw new ValidationException("Tranche request required");
        if (request.getTrancheAmount() == null || request.getTrancheAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new ValidationException("Tranche amount must be positive");

        DisbursementEntity disb = dao.findDisbursementById(disbursementId);
        if (disb == null) throw new ResourceNotFoundException("Disbursement not found: " + disbursementId);

        // Idempotency: return existing if present
        if (request.getIdempotencyKey() != null) {
            TrancheEntity existing = dao.findByDisbursementIdAndIdempotencyKey(disbursementId, request.getIdempotencyKey());
            if (existing != null) return toTrancheResponse(existing);
        }

        // Validate sum of existing tranches <= netDisbursed
        BigDecimal existingSum = (disb.getTranches() == null ? BigDecimal.ZERO :
                disb.getTranches().stream()
                        .map(t -> t.getTrancheAmount() != null ? t.getTrancheAmount() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .setScale(2, MONEY_ROUNDING);

        BigDecimal allowed = (disb.getNetDisbursedAmount() == null ? BigDecimal.ZERO : disb.getNetDisbursedAmount())
                .subtract(existingSum).setScale(2, MONEY_ROUNDING);

        if (request.getTrancheAmount().setScale(2, MONEY_ROUNDING).compareTo(allowed) > 0) {
            throw new SomethingWentWrongException("Tranche exceeds available disbursement. Allowed: " + allowed);
        }

        TrancheEntity tranche = new TrancheEntity();
        tranche.setDisbursement(disb);
        tranche.setTrancheAmount(request.getTrancheAmount().setScale(2, MONEY_ROUNDING));
        tranche.setTrancheDate(request.getTrancheDate() != null ? request.getTrancheDate() : LocalDate.now());
        tranche.setStatus(TrancheStatus.PENDING);
        tranche.setInitiatedBy(request.getInitiatedBy());
        tranche.setInitiatedAt(LocalDateTime.now());
        tranche.setIdempotencyKey(request.getIdempotencyKey());

        dao.saveTranche(tranche);

        saveAudit(tranche, "TRANCHE_CREATED", request.getInitiatedBy(), "Tranche created idempotencyKey:" + request.getIdempotencyKey());

        return toTrancheResponse(tranche);
    }

    @Override
    @Transactional
    public TrancheResponseDTO initiateTranche(String trancheId, String externalTxnId, String initiatedBy) {
        TrancheEntity tranche = dao.findTrancheById(trancheId);
        if (tranche == null) throw new ResourceNotFoundException("Tranche not found: " + trancheId);

        if (tranche.getStatus() == TrancheStatus.SUCCESS)
            throw new SomethingWentWrongException("Tranche already successful");

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

        // Update aggregate summary (sum all CONFIRMED tranches across the loan)
        DisbursementEntity disb = tranche.getDisbursement();
        LoanApplicationEntity loan = disb.getLoanApplication();
        LoanDisbursementEntity summary = ensureSummary(loan);

        BigDecimal total = recalcTotalDisbursed(loan.getId()); // from ALL disbursements' CONFIRMED tranches
        summary.setTotalDisbursed(total);
        dao.saveLoanDisbursement(summary);

        saveAudit(tranche, "TRANCHE_CONFIRMED", confirmedBy, "Confirmed and summary updated");
        return toTrancheResponse(tranche);
    }

    /* ============================== Repayment ============================== */

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
        repayment.setRepaymentAmount(request.getRepaymentAmount().setScale(2, MONEY_ROUNDING));
        repayment.setRepaymentDate(request.getRepaymentDate() != null ? request.getRepaymentDate() : LocalDate.now());
        repayment.setPaidBy(request.getPaidBy());

        dao.saveRepayment(repayment);

        // Note: add your outstanding balance logic here (e.g., update on Disbursement/Loan summary)

        return toRepaymentResponse(repayment);
    }

    /* ================================ Reads ================================ */

    @Override
    @Transactional(readOnly = true)
    public List<DisbursementResponseDTO> getDisbursementsByLoan(String loanApplicationId) {
        return dao.findDisbursementsByLoanId(loanApplicationId)
                 .stream()
                 .map(this::toDisbursementResponse)
                 .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrancheResponseDTO> getTranchesByDisbursement(String disbursementId) {
        return dao.findTranchesByDisbursementId(disbursementId)
                 .stream()
                 .map(this::toTrancheResponse)
                 .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepaymentResponseDTO> getRepaymentsByLoan(String loanApplicationId) {
        return dao.findRepaymentsByLoanId(loanApplicationId)
                 .stream()
                 .map(this::toRepaymentResponse)
                 .collect(Collectors.toList());
    }

    /* ============================ Reconciliation =========================== */

    @Override
    @Transactional
    public void reconcileExternalTransaction(String externalTxnId, String status, BigDecimal amount, LocalDate date) {
        // Find tranche by externalTxnId
        String hql = "from TrancheEntity t where t.externalTransactionId = :ext";
        TrancheEntity t = (TrancheEntity) sessionFactory.getCurrentSession()
                .createQuery(hql, TrancheEntity.class)
                .setParameter("ext", externalTxnId)
                .uniqueResult();

        ReconciliationEntity recon = new ReconciliationEntity();
        recon.setExternalTxnId(externalTxnId);
        recon.setExternalAmount(amount != null ? amount.setScale(2, MONEY_ROUNDING) : null);
        recon.setExternalDate(date);
        recon.setReconciledAt(LocalDateTime.now());

        if (t == null) {
            recon.setStatus("PENDING");
            recon.setRemarks("No matching tranche found");
            dao.saveReconciliation(recon);
            return;
        }

        // Found: validate amount & status
        boolean amountMatch = amount != null && t.getTrancheAmount() != null
                && t.getTrancheAmount().setScale(2, MONEY_ROUNDING).compareTo(amount.setScale(2, MONEY_ROUNDING)) == 0;
        boolean statusSuccess = "SUCCESS".equalsIgnoreCase(status);

        if (amountMatch && statusSuccess) {
            t.setStatus(TrancheStatus.SUCCESS);
            t.setConfirmedAt(LocalDateTime.now());
            dao.saveTranche(t);

            // Update summary from ALL CONFIRMED tranches of the loan
            String loanId = t.getDisbursement().getLoanApplication().getId();
            LoanDisbursementEntity summary = ensureSummary(t.getDisbursement().getLoanApplication());
            BigDecimal total = recalcTotalDisbursed(loanId);
            summary.setTotalDisbursed(total);
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

    /* ================================ Helpers ============================== */

    private LoanDisbursementEntity ensureSummary(LoanApplicationEntity loan) {
        LoanDisbursementEntity summary = dao.findLoanDisbursementByLoanId(loan.getId());
        if (summary == null) {
            summary = new LoanDisbursementEntity();
            summary.setLoanApplication(loan);
            summary.setTotalDisbursed(BigDecimal.ZERO.setScale(2, MONEY_ROUNDING));
            dao.saveLoanDisbursement(summary);
        }
        return summary;
    }

    /**
     * Sum of ALL CONFIRMED tranches (TrancheEntity) across ALL disbursements for a loan.
     * This avoids drift and ignores PENDING/INITIATED tranches.
     */
    private BigDecimal recalcTotalDisbursed(String loanApplicationId) {
        List<DisbursementEntity> disbursements = dao.findDisbursementsByLoanId(loanApplicationId);
        BigDecimal total = BigDecimal.ZERO;
        for (DisbursementEntity d : disbursements) {
            if (d.getTranches() == null) continue;
            for (TrancheEntity t : d.getTranches()) {
                if (t.getStatus() == TrancheStatus.SUCCESS && t.getTrancheAmount() != null) {
                    total = total.add(t.getTrancheAmount());
                }
            }
        }
        return total.setScale(2, MONEY_ROUNDING);
    }

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
        dto.setStatus(t.getStatus().getValue());
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
