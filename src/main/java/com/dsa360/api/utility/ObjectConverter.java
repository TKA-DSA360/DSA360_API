package com.dsa360.api.utility;

import org.springframework.stereotype.Component;

import com.dsa360.api.dto.CustomerDTO;
import com.dsa360.api.dto.DocumentDTO;
import com.dsa360.api.dto.DsaKycDto;
import com.dsa360.api.entity.CustomerEntity;
import com.dsa360.api.entity.DsaApplicationEntity;
import com.dsa360.api.entity.DocumentEntity;
import com.dsa360.api.entity.DsaKycEntity;

@Component
public class ObjectConverter {

	public Object dtoToEntity(Object sourceObject) {

		if (sourceObject instanceof DsaKycDto) {
			var kycEntity = new DsaKycEntity();
			DsaKycDto dto = (DsaKycDto) sourceObject;

			var dsaRegistrationEntity = new DsaApplicationEntity();

			dsaRegistrationEntity.setDsaApplicationId(dto.getDsaApplicationId());

			kycEntity.setDsaKycId(dto.getDsaKycId());
			kycEntity.setDsaApplicationId(dsaRegistrationEntity);
			kycEntity.setBankName(dto.getBankName());
			kycEntity.setAccountNumber(dto.getAccountNumber());
			kycEntity.setIfscCode(dto.getIfscCode());

			kycEntity.setPassport(dto.getPassportFile().getOriginalFilename());
			kycEntity.setDrivingLicence(dto.getDrivingLicenceFile().getOriginalFilename());
			kycEntity.setAadharCard(dto.getAadharCardFile().getOriginalFilename());
			kycEntity.setPanCard(dto.getPanCardFile().getOriginalFilename());
			kycEntity.setPhotograph(dto.getPhotographFile().getOriginalFilename());
			kycEntity.setAddressProof(dto.getAddressProofFile().getOriginalFilename());
			kycEntity.setBankPassbook(dto.getBankPassbookFile().getOriginalFilename());

			kycEntity.setApprovalStatus(dto.getApprovalStatus());
			kycEntity.setAttempt(dto.getAttempt());

			return kycEntity;

		}

		if (sourceObject instanceof CustomerDTO) {
			var customerEntity = new CustomerEntity();
			var customerDTO = (CustomerDTO) sourceObject;

			customerEntity.setId(customerDTO.getId());
			customerEntity.setName(customerDTO.getName());
			customerEntity.setEmail(customerDTO.getEmail());
			customerEntity.setPhoneNumber(customerDTO.getPhoneNumber());
			customerEntity.setPermanentAddress(customerDTO.getPermanentAddress());
			customerEntity.setCurrentAddress(customerEntity.getCurrentAddress());

			var dsaAgent = new DsaApplicationEntity();
			dsaAgent.setDsaApplicationId(customerDTO.getDsaAgentId());
			customerEntity.setDsaAgentId(dsaAgent);

			return customerEntity;

		}

		if (sourceObject instanceof DocumentDTO) {
			var documentEntity = new DocumentEntity();
			var documentDTO = (DocumentDTO) sourceObject;

			documentEntity.setId(documentDTO.getId());
			documentEntity.setDocumentName(documentDTO.getDocumentName());
			documentEntity.setDocumentType(documentDTO.getDocumentType().getDisplayName());
			documentEntity.setStatus(documentDTO.getStatus());
			documentEntity.setComment(documentDTO.getComment());

			var customerEntity = new CustomerEntity();
			customerEntity.setId(documentDTO.getCustomerId());
			documentEntity.setCustomer(customerEntity);
			return documentEntity;

		}

		return null;

	}
	
	public Object entityToDto(Object sourceObject) {
		if(sourceObject instanceof CustomerEntity) {
			var  customerEntity=(CustomerEntity) sourceObject;
			var customerDTO=new CustomerDTO();
			return customerDTO;
		}
		return null;
	}

}
