package com.dsa360.api.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer_documents")
public class DocumentEntity extends  BaseEntity{

	@Id
	@Column(name = "id", nullable = false, updatable = false)
	private String id;

	@Column(name = "document_name", nullable = false, length = 100)
	private String documentName;

	@Column(name = "comment", nullable = false, columnDefinition = "TEXT")
	private String comment;

	@Column(name = "document_type", nullable = false, length = 50)
	private String documentType; // e.g., "ID Proof", "Income Proof"

	@Column(name = "status", nullable = false, length = 20)
	private String status; // e.g., "Uploaded", "Verified", "Rejected"

	@ManyToOne
	@JoinColumn(name = "customer_id", nullable = false)
	@JsonBackReference
	
	private CustomerEntity customer;

}
