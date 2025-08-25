package com.dsa360.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dsa360.api.dto.loan.DisbursementRequestDTO;
import com.dsa360.api.dto.loan.DisbursementResponseDTO;
import com.dsa360.api.dto.loan.RepaymentRequestDTO;
import com.dsa360.api.dto.loan.RepaymentResponseDTO;
import com.dsa360.api.dto.loan.TrancheRequestDTO;
import com.dsa360.api.dto.loan.TrancheResponseDTO;
import com.dsa360.api.service.LoanDisbursementService;

@RestController
@RequestMapping("/api/loans")
public class LoanDisbursementController {

    private final LoanDisbursementService loanDisbursementService;

    public LoanDisbursementController(LoanDisbursementService loanDisbursementService) {
        this.loanDisbursementService = loanDisbursementService;
    }

    // Create loan disbursement
    @PostMapping("/{loanApplicationId}/disbursements")
    public ResponseEntity<DisbursementResponseDTO> createDisbursement(
            @PathVariable String loanApplicationId,
            @RequestBody DisbursementRequestDTO request) {
        return ResponseEntity.ok(loanDisbursementService.createDisbursement(loanApplicationId, request));
    }

    // Add tranche to disbursement
    @PostMapping("/disbursements/{disbursementId}/tranches")
    public ResponseEntity<TrancheResponseDTO> addTranche(
            @PathVariable String disbursementId,
            @RequestBody TrancheRequestDTO request) {
        return ResponseEntity.ok(loanDisbursementService.addTranche(disbursementId, request));
    }

    // Make repayment
    @PostMapping("/{loanApplicationId}/repayments")
    public ResponseEntity<RepaymentResponseDTO> makeRepayment(
            @PathVariable String loanApplicationId,
            @RequestBody RepaymentRequestDTO request) {
        return ResponseEntity.ok(loanDisbursementService.makeRepayment(loanApplicationId, request));
    }

    // Get disbursements by loan
    @GetMapping("/{loanApplicationId}/disbursements")
    public ResponseEntity<List<DisbursementResponseDTO>> getDisbursements(
            @PathVariable String loanApplicationId) {
        return ResponseEntity.ok(loanDisbursementService.getDisbursementsByLoan(loanApplicationId));
    }

    // Get tranches by disbursement
    @GetMapping("/disbursements/{disbursementId}/tranches")
    public ResponseEntity<List<TrancheResponseDTO>> getTranches(
            @PathVariable String disbursementId) {
        return ResponseEntity.ok(loanDisbursementService.getTranchesByDisbursement(disbursementId));
    }

    // Get repayments by loan
    @GetMapping("/{loanApplicationId}/repayments")
    public ResponseEntity<List<RepaymentResponseDTO>> getRepayments(
            @PathVariable String loanApplicationId) {
        return ResponseEntity.ok(loanDisbursementService.getRepaymentsByLoan(loanApplicationId));
    }
}
