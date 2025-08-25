package com.dsa360.api.entity.loan;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.dsa360.api.constants.ApprovalStatus;
import com.dsa360.api.entity.BaseEntity;
import com.dsa360.api.entity.CustomerEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loan_applications")
public class LoanApplicationEntity extends  BaseEntity{

    @Id
    private String id; 

    @ManyToOne
	@JoinColumn(name = "customer_id", nullable = false)
    @JsonBackReference
	private CustomerEntity customer;

    @Column(name = "loan_amount", nullable = false)
    private BigDecimal loanAmount; // The amount of loan being requested

    @Column(name = "loan_tenure", nullable = false)
    private Integer loanTenure; // Loan tenure in months

    @Column(name = "loan_purpose", nullable = false)
    private String loanPurpose; // Purpose of the loan (e.g., Home, Car, Personal)

    @Column(name = "status")
    private String approvalStatus = ApprovalStatus.PENDING.getValue(); // Status of the loan application;

    @Column(name = "interest_rate")
    private BigDecimal interestRate; // Interest rate for the loan

    @Column(name = "emi")
    private BigDecimal emi; // Estimated Monthly Installment

    // Additional relevant fields

    @Column(name = "application_date", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate applicationDate; // Date of application submission

    @Column(name = "payment_frequency", nullable = false)
    private String paymentFrequency; // Frequency of loan payments (e.g., monthly, bi-weekly)

    @Column(name = "income_details")
    private String incomeDetails; // Information about applicant's income

    @Column(name = "employment_status")
    private String employmentStatus; // Employment status (e.g., employed, self-employed)

    @Column(name = "loan_purpose_description")
    private String loanPurposeDescription; // Detailed description of loan purpose

    @Column(name = "credit_score")
    private Integer creditScore; // Applicant's credit score

    @Column(name = "rejection_reason")
    private String rejectionReason; // Reason for rejection (if applicable)
}
