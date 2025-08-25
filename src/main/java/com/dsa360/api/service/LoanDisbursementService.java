package com.dsa360.api.service;

import java.util.List;
import com.dsa360.api.dto.loan.*;

public interface LoanDisbursementService {
    DisbursementResponseDTO createDisbursement(String loanApplicationId, DisbursementRequestDTO request);
    TrancheResponseDTO addTranche(String disbursementId, TrancheRequestDTO request); // idempotent
    TrancheResponseDTO initiateTranche(String trancheId, String externalTxnId, String initiatedBy);
    TrancheResponseDTO confirmTranche(String trancheId, String confirmedBy);
    RepaymentResponseDTO makeRepayment(String loanApplicationId, RepaymentRequestDTO request);

    List<DisbursementResponseDTO> getDisbursementsByLoan(String loanApplicationId);
    List<TrancheResponseDTO> getTranchesByDisbursement(String disbursementId);
    List<RepaymentResponseDTO> getRepaymentsByLoan(String loanApplicationId);

    // reconciliation entry point (simplified)
    void reconcileExternalTransaction(String externalTxnId, String status, java.math.BigDecimal amount, java.time.LocalDate date);
}
