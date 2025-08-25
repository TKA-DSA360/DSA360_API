package com.dsa360.api.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dsa_kyc")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class DsaKycEntity extends  BaseEntity{

	@Id // KYC-2025-VC763456
	private String dsaKycId;

	@OneToOne
	@JoinColumn(name = "dsa_reg_id")
	private DsaApplicationEntity dsaApplicationId;

	private String bankName;
	private String accountNumber;
	private String ifscCode;

	private String passport;
	private String drivingLicence;
	private String aadharCard;
	private String panCard;
	private String photograph;
	private String addressProof;
	private String bankPassbook;

	private String approvalStatus;
	private int attempt=1;

}
