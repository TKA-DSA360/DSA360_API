package com.dsa360.api.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import com.dsa360.api.constants.ApprovalStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DsaKycDto {

	private String dsaKycId;

	@NotBlank(message = "DSA application ID is required")
    private String dsaApplicationId;

    @NotBlank(message = "Bank name is required")
    @Size(min = 2, max = 50, message = "Bank name should be between 2 and 50 characters")
    private String bankName;

    @NotBlank(message = "Account number is required")
    @Pattern(regexp = "\\d{9,18}", message = "Account number must be between 9 and 18 digits")
    private String accountNumber;

    @NotBlank(message = "IFSC code is required")
    @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "Invalid IFSC code format")
    private String ifscCode;


	private String approvalStatus = ApprovalStatus.PENDING.getValue();
	private int attempt=1;

	private MultipartFile passportFile;
	private MultipartFile drivingLicenceFile;
	private MultipartFile aadharCardFile;
	private MultipartFile panCardFile;
	private MultipartFile photographFile;
	private MultipartFile addressProofFile;
	private MultipartFile bankPassbookFile;

}
