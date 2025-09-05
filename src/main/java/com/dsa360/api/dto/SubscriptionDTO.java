package com.dsa360.api.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubscriptionDTO {
    private String subscriptionId;
    private String tenantId;
    private String planId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String billingStatus;
    private String paymentGatewayId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}