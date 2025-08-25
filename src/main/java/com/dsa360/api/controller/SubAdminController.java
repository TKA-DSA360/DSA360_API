package com.dsa360.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dsa360.api.dto.CustomResponse;
import com.dsa360.api.dto.DSAApplicationDTO;
import com.dsa360.api.entity.DsaKycEntity;
import com.dsa360.api.service.DSAService;
import com.dsa360.api.service.DsaKycService;

@RestController
@RequestMapping("/sub-admin")
public class SubAdminController {

	@Autowired
	private DSAService dsaService;

	@Autowired
	private DsaKycService kycService;

	@GetMapping("/notify-approval-status")
	@PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_SUB_ADMIN')")
	public ResponseEntity<String> notifyReview(@RequestParam String id, @RequestParam String approvalStatus,
		@RequestParam String type) {

		String status = dsaService.notifyReview(id, approvalStatus, type);
		return new ResponseEntity<>(status, HttpStatus.OK);

	}

	@GetMapping("/get-dsa-application-by-id/{dsaId}")
	@PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_SUB_ADMIN')")
	public ResponseEntity<DSAApplicationDTO> getDsaById(@PathVariable String dsaId) {
		DSAApplicationDTO dsaApplication = dsaService.getDSAById(dsaId);
		return new ResponseEntity<>(dsaApplication, HttpStatus.OK);
	}

	@GetMapping("/get-all-dsa-application")
	@PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_SUB_ADMIN')")
	public ResponseEntity<List<DSAApplicationDTO>> getAllDsaApplications() {

		List<DSAApplicationDTO> dsaApplications = dsaService.getAllDsaApplication();

		return new ResponseEntity<>(dsaApplications, HttpStatus.OK);

	}

	@GetMapping("/get-all-kycs")
	@PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_SUB_ADMIN')")
	public ResponseEntity<List<DsaKycEntity>> getAllKycs() {

		List<DsaKycEntity> allKycs = kycService.getAllKycs();

		return new ResponseEntity<>(allKycs, HttpStatus.OK);

	}

	@GetMapping("/get-kyc-by-dsaId/{dsaId}")
	@PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_SUB_ADMIN')")
	public ResponseEntity<DsaKycEntity> getDsaKycByDsaId(@PathVariable String dsaId) {

		DsaKycEntity dsaKyc = dsaService.getDsaKycByDsaId(dsaId);

		return new ResponseEntity<>(dsaKyc, HttpStatus.OK);

	}

	@GetMapping("/email-verification-request/{dsaId}")
	@PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_SUB_ADMIN')")
	public ResponseEntity<CustomResponse> emailVerificationRequest(@PathVariable String dsaId) {

		dsaService.emailVerificationRequest(dsaId);
		var customResponse = new CustomResponse("Done", 200);

		return new ResponseEntity<>(customResponse, HttpStatus.OK);

	}

}
