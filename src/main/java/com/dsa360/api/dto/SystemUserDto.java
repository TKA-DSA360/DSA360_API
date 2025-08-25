package com.dsa360.api.dto;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author RAM
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemUserDto {

	@NotBlank(message = "Username is mandatory")
	private String username;

	@NotBlank(message = "Password is mandatory")
	@Size(min = 8, message = "Password must be at least 8 characters long")
	private String password;

	@NotBlank(message = "Security question is mandatory")
	private String question;

	@NotBlank(message = "Answer is mandatory")
	private String answer;

	

	@NotNull(message = "Roles are mandatory")
	private List<String> roles;

	@NotNull(message = "Regions are mandatory")
	private List<String> regions;

	@NotBlank(message = "Status is mandatory")
	private String status = "Active"; //Suspended  Deactivated  Active

	@NotBlank(message = "Status Reason is mandatory")
	private String statusReason = "KYC Verified";

	@NotBlank(message = "DSA Application ID is mandatory")
	@Pattern(regexp = "[A-Z]{3}-\\d{4}-[A-Z]{2}\\d{6}", message = "Application ID must follow the format XXX-YYYY-XX######")
	private String dsaApplicationId;

}
