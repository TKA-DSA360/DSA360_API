package com.dsa360.api.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author RAM
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {

	private String id;
	
	@NotBlank(message = "Role name is required")
	@Pattern(regexp = "^ROLE_[A-Z]+(_[A-Z]+)*$", message = "Role name must start with 'ROLE_' and contain uppercase letters and underscores")
	private String name;

}
