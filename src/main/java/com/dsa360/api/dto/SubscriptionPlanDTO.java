package com.dsa360.api.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class SubscriptionPlanDTO {
	private String planId;
	
	@NotBlank(message = "Plan name is required")
	@Size(max = 100, message = "Plan name cannot exceed 100 characters")
	private String planName;
	
	@NotBlank(message = "Plan type is required")
	@Size(max = 50, message = "Plan type cannot exceed 50 characters")
	private String planType; // e.g. Basic, Pro, Enterprise

	@Size(max = 500, message = "Description cannot exceed 500 characters")
	private String description;

	@Positive(message = "Monthly price must be greater than 0")
	private double monthlyPrice;

	@Min(value = 1, message = "At least 1 user is required")
	private int maxUsers;

	@Min(value = 0, message = "Trial period cannot be negative")
	private int trialPeriodDays;

	@NotBlank(message = "Billing cycle is required")
	private String billingCycle; // e.g. Monthly, Yearly

	@NotBlank(message = "Currency is required")
	private String currency; // e.g. INR, USD

	private String features; // Optional features

}