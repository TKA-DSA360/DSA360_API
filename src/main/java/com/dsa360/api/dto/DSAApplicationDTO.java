package com.dsa360.api.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.dsa360.api.constants.ApprovalStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author RAM
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DSAApplicationDTO {
	
	
	private String dsaApplicationId;

	@NotBlank(message = "First name is mandatory")
	private String firstName;

	@NotBlank(message = "Middle name is mandatory")
	private String middleName;

	@NotBlank(message = "Last name is mandatory")
	private String lastName;

	@NotBlank(message = "Gender is mandatory")
	@Pattern(regexp = "Male|Female|Other", message = "Gender must be Male, Female, or Other")
	private String gender;

	@NotNull(message = "Date of birth is mandatory")
	@Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Date of Birth must be in YYYY-MM-DD format")
	private String dateOfBirth;

	@NotBlank(message = "Nationality is mandatory")
	private String nationality;

	@NotBlank(message = "Contact number is mandatory")
	@Pattern(regexp = "^[1-9]\\d{9}$", message = "Contact number must be a 10-digit number and cannot start with 0")
	private String contactNumber;

	@NotBlank(message = "Email address is mandatory")
	@Email(message = "Email should be valid")
	private String emailAddress;

	@NotBlank(message = "Street address is mandatory")
	private String streetAddress;

	@NotBlank(message = "City is mandatory")
	private String city;

	@NotBlank(message = "State is mandatory")
	private String state;

	@NotBlank(message = "Postal code is mandatory")
	@Pattern(regexp = "\\d{6}", message = "Postal code must be a 6-digit number")
	private String postalCode;

	@NotBlank(message = "Country is mandatory")
	private String country;

	@NotBlank(message = "Preferred language is mandatory")
	private String preferredLanguage;

	@NotBlank(message = "Educational Qualification is mandatory")
	private String educationalQualifications;

	@NotBlank(message = "experience is mandatory  YES/NO")
	private String experience;

	@NotBlank(message = "Associated With Other DSA is mandatory  YES/NO")
	private String isAssociatedWithOtherDSA;

	@NotBlank(message = "Institution Name is mandatory - if no (NA)")
	private String associatedInstitutionName;

	@NotBlank(message = "Referral Source is mandatory - if no (NA)")
	private String referralSource;
	
	private String approvalStatus = ApprovalStatus.PENDING.getValue();
	private boolean emailVerified;

}
