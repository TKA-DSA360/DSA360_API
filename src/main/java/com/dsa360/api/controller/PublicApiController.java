package com.dsa360.api.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dsa360.api.annotatios.ValidFile;
import com.dsa360.api.dto.ContactUsDTO;
import com.dsa360.api.dto.DSAApplicationDTO;
import com.dsa360.api.dto.DsaKycDto;
import com.dsa360.api.service.CustomerService;
import com.dsa360.api.service.DSAService;

/**
 * @author RAM
 *
 */
@RestController
@RequestMapping("/public")
public class PublicApiController {

	@Autowired
	private DSAService dsaService;
	@Autowired
	private CustomerService customerService;

	@PostMapping("/dsa-application")
	public ResponseEntity<DSAApplicationDTO> dsaApplication(@RequestBody @Valid DSAApplicationDTO dsaRegistrationDTO) {

		DSAApplicationDTO dsaRegistration = dsaService.dsaApplication(dsaRegistrationDTO);
		return new ResponseEntity<>(dsaRegistration, HttpStatus.CREATED);

	}

	@GetMapping("/get-dsa-application/{dsaId}")
	public ResponseEntity<DSAApplicationDTO> getDsaApplicationData(@PathVariable String dsaId) {

		DSAApplicationDTO dsaById = dsaService.getDSAById(dsaId);

		return new ResponseEntity<>(dsaById, HttpStatus.OK);

	}

	@PostMapping("/syatem-user-kyc")
	public ResponseEntity<String> systemUserKyc(@RequestParam @ValidFile MultipartFile passport,
			@RequestParam @ValidFile MultipartFile drivingLicence, @RequestParam @ValidFile MultipartFile aadharCard,
			@RequestParam @ValidFile MultipartFile panCard, @RequestParam @ValidFile MultipartFile photograph,
			@RequestParam @ValidFile MultipartFile addressProof, @RequestParam @ValidFile MultipartFile bankPassbook,

			@RequestParam String dsaApplicationId, @RequestParam String bankName, @RequestParam String accountNumber,
			@RequestParam String ifscCode) {

		var dsa_KYC_DTO = new DsaKycDto();

		String message = null;

		dsa_KYC_DTO.setDsaApplicationId(dsaApplicationId);
		dsa_KYC_DTO.setBankName(bankName);
		dsa_KYC_DTO.setAccountNumber(accountNumber);
		dsa_KYC_DTO.setIfscCode(ifscCode);

		dsa_KYC_DTO.setPassportFile(passport);
		dsa_KYC_DTO.setDrivingLicenceFile(drivingLicence);
		dsa_KYC_DTO.setAadharCardFile(aadharCard);
		dsa_KYC_DTO.setPanCardFile(panCard);
		dsa_KYC_DTO.setPhotographFile(photograph);
		dsa_KYC_DTO.setAddressProofFile(addressProof);
		dsa_KYC_DTO.setBankPassbookFile(bankPassbook);

		message = dsaService.systemUserKyc(dsa_KYC_DTO);

		return new ResponseEntity<>(message, HttpStatus.CREATED);

	}

	@GetMapping("/verify-email/{dsaId}")
	public ResponseEntity<String> verifyEmail(@PathVariable String dsaId, @RequestParam String token) {

		dsaService.verifyEmail(dsaId, token);
		try {
			String htmlContent = "<!DOCTYPE html>" + "<html lang='en'>" + "<head>" + "<meta charset='UTF-8'>"
					+ "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
					+ "<title>Email Verified</title>" + "<script>"
					+ "alert('Email Verification Successfully Completed!!');"
					+ "window.location.href = 'http://localhost:4200/';" + "</script>" + "</head>" + "<body>"
					+ "</body>" + "</html>";

			var headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8");
			return new ResponseEntity<>(htmlContent, headers, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>("Email Verification Successfully Completed !!", HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("/contact-us")
	public ResponseEntity<String> contactUs(@RequestBody @Valid ContactUsDTO contactUsDTO) {
		String message = customerService.contactUs(contactUsDTO);

		return message != null ? new ResponseEntity<>(message, HttpStatus.CREATED)
				: new ResponseEntity<>("Contact Us Failed", HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
