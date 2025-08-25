package com.dsa360.api.dto;

public class CustomResponse {
	
	private String message;
	private int ststusCode;
	
	public CustomResponse() {
		// TODO Auto-generated constructor stub
	}

	public CustomResponse(String message, int ststusCode) {
		super();
		this.message = message;
		this.ststusCode = ststusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStstusCode() {
		return ststusCode;
	}

	public void setStstusCode(int ststusCode) {
		this.ststusCode = ststusCode;
	}
	
	

}
