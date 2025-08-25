package com.dsa360.api.entity.loan;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.dsa360.api.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "loan_condition")
public class LoanConditioEntity extends  BaseEntity{

	@Id
	private Long id;

	private String bankName;
	private String loanType;
	private double interestRate;
	private String minCreditScore;
	private double processingFee;
}
