package com.dsa360.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanConditionDto {

	private String id;

	private String bankName;
	private String loanType;
	private double interestRate;
	private String minCreditScore;
	private double processingFee;
}
