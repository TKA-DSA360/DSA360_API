package com.dsa360.api.entity.master;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.dsa360.api.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Table(name = "tenants")
@Entity
@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class TenantEntity extends BaseEntity {
	@Id
	@Column(name = "tenant_id")
	private String tenantId;

	@Column(name = "tenant_name")
	private String tenantName;
	
	@Column(name = "email")
	private String email;

	@Column(name = "db_url")
	private String dbUrl;

	@Column(name = "db_username")
	private String dbUsername;

	@Column(name = "db_password")
	private String dbPassword;

	@Column(name = "subscription_status")
	private String subscriptionStatus;

	@OneToOne(mappedBy = "tenant", cascade = CascadeType.ALL)
	private SubscriptionEntity subscription;

}