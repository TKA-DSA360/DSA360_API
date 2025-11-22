package com.dsa360.api.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class LoginRequestDto {
	
	@NotBlank(message = "Username is required")
	private String username;
	
	@NotBlank(message = "Password is required")
	private String password;
	
	private String tenantId;

}
