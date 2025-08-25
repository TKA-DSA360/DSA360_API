package com.dsa360.api.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dsa360.api.constants.DocumentType;
import com.dsa360.api.dto.CustomerDTO;
import com.dsa360.api.dto.DocumentDTO;
import com.dsa360.api.dto.LoanApplicationDTO;
import com.dsa360.api.entity.CustomerEntity;
import com.dsa360.api.service.CustomerService;

@RestController
@RequestMapping("/agent")
public class AgentController {

	@Autowired
	private CustomerService service;

	@PreAuthorize("hasRole('ROLE_AGENT')")
	@PostMapping("/create-customer")
	public ResponseEntity<String> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
		String customerId = service.createCustomer(customerDTO);

		return new ResponseEntity<>(customerId, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ROLE_AGENT')")
	@PostMapping("/customer-loan-application")
	public ResponseEntity<String> customerLoanApplication(@Valid @RequestBody LoanApplicationDTO loanApplicationDTO) {
		String loadApplicationId = service.customerLoanApplication(loanApplicationDTO);

		return new ResponseEntity<>(loadApplicationId, HttpStatus.OK);
	}

	@PostMapping("/documents/upload/{customerId}")
	public ResponseEntity<String> uploadDocument(@PathVariable String customerId, @RequestParam String documentTypeStr,
			@RequestParam String comment, @RequestParam MultipartFile file) {

		DocumentType documentType;
		try {
			documentType = DocumentType.fromDisplayName(documentTypeStr);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body("Invalid document type: " + documentTypeStr);
		}

		DocumentDTO documentDTO = new DocumentDTO();
		documentDTO.setCustomerId(customerId);
		documentDTO.setDocumentName(file.getOriginalFilename());
		documentDTO.setDocumentType(documentType);
		documentDTO.setComment(comment);
		documentDTO.setFile(file);

		service.uploadDocument(customerId, documentDTO);
		return ResponseEntity.ok("Document uploaded successfully for customer ID: " + customerId);
	}

	@GetMapping("/customers")
	public ResponseEntity<List<CustomerEntity>> getAllCustomers() {
		List<CustomerEntity> customers = service.getAllCustomers();
		return ResponseEntity.ok(customers);
	}

	@GetMapping("/customers/dsa/{dsaAgentId}")
	public ResponseEntity<List<CustomerEntity>> getCustomersByDsaAgent(@PathVariable String dsaAgentId) {
		List<CustomerEntity> customers = service.getCustomersByDsaAgentId(dsaAgentId);

		if (customers.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(customers);
	}

	@GetMapping("/get-customer/{id}")
	public ResponseEntity<CustomerEntity> getCustomerById(@PathVariable String id) {
		CustomerEntity customer = service.getCustomerById(id);
		return ResponseEntity.ok(customer);
	}

	@PutMapping("/{id}")
	public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable String id,
			@Valid @RequestBody CustomerDTO customerDTO) {
		CustomerDTO updatedCustomer = service.updateCustomer(customerDTO);
		return ResponseEntity.ok(updatedCustomer);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> cancelCustomerLoanApplication(@PathVariable String id) {
		service.cancelCustomerLoanApplication(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{customerId}/documents")
	public ResponseEntity<List<DocumentDTO>> getDocumentsByCustomerId(@PathVariable String customerId) {
		List<DocumentDTO> documents = service.getDocumentsByCustomerId(customerId);
		return ResponseEntity.ok(documents);
	}

	@GetMapping("/{customerId}/documents/{documentId}")
	public ResponseEntity<DocumentDTO> getDocumentById(@PathVariable String customerId,
			@PathVariable String documentId) {
		DocumentDTO document = service.getDocumentById(customerId, documentId);
		return ResponseEntity.ok(document);
	}

	@DeleteMapping("/{customerId}/documents/{documentId}")
	public ResponseEntity<Void> deleteDocument(@PathVariable String customerId, @PathVariable String documentId) {
		service.deleteDocument(customerId, documentId);
		return ResponseEntity.noContent().build();
	}
	
	@PutMapping("/customer/update")
	public ResponseEntity<CustomerDTO> updateCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
		CustomerDTO updatedCustomer = service.updateCustomer(customerDTO);
		return ResponseEntity.ok(updatedCustomer);
	}

}
