package com.dsa360.api.entity.master;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
	@Column(name = "plan_id", nullable = false, unique = true, length = 50)
	
	private String planId;

	@Column(name = "plan_name", nullable = false, length = 100)
	private String planName;

	@Column(name = "description", length = 500)
	private String description;

	@Column(name = "monthly_price", nullable = false)
	private double monthlyPrice;

	@Column(name = "max_users", nullable = false)
	private int maxUsers;

	@Column(name = "trial_period_days", nullable = false)
	private int trialPeriodDays;

	@Column(name = "billing_cycle", nullable = false, length = 50)
	private String billingCycle;

	@Column(name = "currency", nullable = false, length = 10)
	private String currency;

	// Storing as comma-separated string, can be split into list in service
	@Column(name = "features", length = 1000)
	private String features;

}