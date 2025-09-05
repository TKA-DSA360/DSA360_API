package com.dsa360.api.entity.master;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.dsa360.api.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Table(name = "subscriptions")
@Entity
@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionEntity extends  BaseEntity {
	@Id
    @Column(name = "subscription_id")
    private String subscriptionId;

    @OneToOne
    @JoinColumn(name = "tenant_id", nullable = false, unique = true)
    private TenantEntity tenant; // One-to-one with TenantEntity

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    private SubscriptionPlanEntity plan; // Many-to-one with SubscriptionPlanEntity

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "billing_status", nullable = false)
    private String billingStatus; // PAID, PENDING, FAILED, CANCELED

    @Column(name = "payment_gateway_id")
    private String paymentGatewayId;

    
}