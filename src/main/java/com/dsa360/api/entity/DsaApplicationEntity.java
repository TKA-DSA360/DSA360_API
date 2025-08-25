package com.dsa360.api.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.dsa360.api.constants.ApprovalStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dsa_application")
public class DsaApplicationEntity extends  BaseEntity{
	@Id   // DSA-2025-VC652345
	@Column(nullable = false, unique = true)
	private String dsaApplicationId;;

	@Column(nullable = false)
	private String firstName;

	private String middleName;

	@Column(nullable = false)
	private String lastName;

	@Column(nullable = false)
	private String gender;

	@Column(nullable = false)
	private String dateOfBirth;

	@Column(nullable = false)
	private String nationality;

	@Column(nullable = false, unique = true)
	private String contactNumber;

	@Column(nullable = false, unique = true)
	private String emailAddress;

	@Column(nullable = false)
	private String streetAddress;

	@Column(nullable = false)
	private String city;

	@Column(nullable = false)
	private String state;

	@Column(nullable = false)
	private String postalCode;

	@Column(nullable = false)
	private String country;

	@Column(nullable = false)
	private String preferredLanguage;

	@Column(nullable = false)
	private String educationalQualifications;

	@Column(nullable = false)
	private String experience;

	@Column(nullable = false)
	private String isAssociatedWithOtherDSA;

	@Column(nullable = false)
	private String associatedInstitutionName;

	@Column(nullable = false)
	private String referralSource;

	@Column(nullable = false)
	private boolean emailVerified=false;
	
	@Column()
	private String emailVerificationToken;
	
	@Column(nullable = false)
	private String approvalStatus = ApprovalStatus.PENDING.getValue();

}
