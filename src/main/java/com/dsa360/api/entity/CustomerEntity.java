package com.dsa360.api.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.dsa360.api.entity.loan.LoanApplicationEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customers")
public class CustomerEntity extends  BaseEntity {

	@Id
	private String id;

	private String name;
	private String email;
	private String phoneNumber;
	private String permanentAddress;
	private String currentAddress;

	// Many Customers belong to one DSAAgent
	@ManyToOne
	@JoinColumn(name = "dsa_agent_id", nullable = false)
	@JsonManagedReference
	private DsaApplicationEntity dsaAgentId;

	@OneToMany(mappedBy = "customer",fetch = FetchType.LAZY)
	@JsonManagedReference
	private Set<LoanApplicationEntity> loanApplications;

	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@JsonManagedReference
	private Set<DocumentEntity> documents;


}
