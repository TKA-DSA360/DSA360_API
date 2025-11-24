package com.dsa360.api.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class SubscriptionDTO {
	private String subscriptionId;

	@NotBlank(message = "Tenant ID is required")
	private String tenantId;

	@NotBlank(message = "Bank name is required")
	private String planId;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime startDate;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime endDate;
	
	private String billingStatus;
	private String paymentGatewayId;

}