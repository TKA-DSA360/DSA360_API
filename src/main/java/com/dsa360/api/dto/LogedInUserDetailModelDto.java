package com.dsa360.api.dto;

import java.util.List;

/**
 * @author RAM
 *
 */
public class LogedInUserDetailModelDto {

	private String id;
	private String username;
	private List<String> roles;
	private String status;
	private String token;

	public LogedInUserDetailModelDto() {
		// Default constructor
	}

	public LogedInUserDetailModelDto(String id,String username, List<String> roles, String status) {
		super();
		this.id=id;
		this.username = username;
		this.roles = roles;
		this.status = status;
	}

	public LogedInUserDetailModelDto(String id,String username, List<String> roles, String status, String token) {
		super();
		this.id=id;
		this.username = username;
		this.roles = roles;
		this.status = status;
		this.token = token;
	}

	public LogedInUserDetailModelDto(String username, List<String> roles) {
		super();
		this.username = username;
		this.roles = roles;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
