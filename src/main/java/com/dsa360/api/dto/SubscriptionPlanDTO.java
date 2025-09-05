package com.dsa360.api.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubscriptionPlanDTO {
    private String planId;
    private String planName;
    private String description;
    private Double monthlyPrice;
    private Integer maxUsers;
    private Integer maxLoans;
    private Integer apiRateLimit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}