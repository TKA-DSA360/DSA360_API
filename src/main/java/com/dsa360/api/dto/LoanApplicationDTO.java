package com.dsa360.api.dto;

import lombok.Data;

import javax.validation.constraints.*;

import com.dsa360.api.constants.ApprovalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoanApplicationDTO {

    private String id; // Loan application ID as String

    @NotEmpty(message = "Customer ID is required.")
    private String customerId; // ID of the customer applying for the loan

    @NotNull(message = "Loan amount is required.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Loan amount must be greater than zero.")
    private BigDecimal loanAmount; // The amount of loan being requested

    @NotNull(message = "Loan tenure is required.")
    @Min(value = 1, message = "Loan tenure must be at least 1 month.")
    private Integer loanTenure; // Loan tenure in months

    @NotEmpty(message = "Loan purpose is required.")
    private String loanPurpose; // Purpose of the loan (e.g., Home, Car, Personal)

    private String status=ApprovalStatus.PENDING.getValue(); // Current status of the loan application (e.g., "Pending", "Approved", "Rejected")

    @DecimalMin(value = "0.0", message = "Interest rate must be a positive value.")
    private BigDecimal interestRate; // Interest rate for the loan

    private BigDecimal emi; // Estimated Monthly Installment

    // Additional relevant fields

    
    private LocalDate applicationDate; // Date of application submission

    @NotEmpty(message = "Payment frequency is required.")
    private String paymentFrequency; // Frequency of loan payments (e.g., monthly, bi-weekly)

    private String incomeDetails; // Information about applicant's income

    private String employmentStatus; // Employment status (e.g., employed, self-employed)

    private String loanPurposeDescription; // Detailed description of loan purpose
    
    @Min(value = 300, message = "Credit score must be at least 300.")
    @Max(value = 850, message = "Credit score cannot exceed 850.")
    private Integer creditScore; // Applicant's credit score

    private String rejectionReason; // Reason for rejection (if applicable)
}
