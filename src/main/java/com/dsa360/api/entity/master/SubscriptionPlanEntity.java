package com.dsa360.api.entity.master;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.dsa360.api.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Table(name = "subscription_plans")
@Entity
@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanEntity extends BaseEntity {
    @Id
    @Column(name = "plan_id")
    private String planId;

    @Column(name = "plan_name", nullable = false)
    private String planName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "monthly_price")
    private Double monthlyPrice;

    @Column(name = "max_users")
    private Integer maxUsers;

    @Column(name = "max_loans")
    private Integer maxLoans; // NBFC-specific

    @Column(name = "api_rate_limit")
    private Integer apiRateLimit;

    
}