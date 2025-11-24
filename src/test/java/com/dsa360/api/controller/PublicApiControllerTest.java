package com.dsa360.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import com.dsa360.api.dto.DSAApplicationDTO;
import com.dsa360.api.service.DSAService;

@SpringBootTest
@AutoConfigureMockMvc
public class PublicApiControllerTest {

	@Mock
	private DSAService dsaService;

	@Autowired
	private MockMvc mockMvc;

	@InjectMocks
	private PublicApiController publicApiController;

	//@Test
	void testDsaApplication_Success() {
		// Arrange
		DSAApplicationDTO inputDto = new DSAApplicationDTO();
		inputDto.setDsaApplicationId("DSA123");
		inputDto.setFirstName("John");
		inputDto.setMiddleName("A");
		inputDto.setLastName("Doe");
		inputDto.setGender("Male");
		inputDto.setDateOfBirth("1990-01-01");
		inputDto.setNationality("Indian");
		inputDto.setContactNumber("9873543210");
		inputDto.setEmailAddress("aa.doe@example.com");
		inputDto.setStreetAddress("123 Main St");
		inputDto.setCity("Mumbai");
		inputDto.setState("Maharashtra");
		inputDto.setPostalCode("400001");
		inputDto.setCountry("India");
		inputDto.setPreferredLanguage("English");
		inputDto.setEducationalQualifications("B.Tech");
		inputDto.setExperience("YES");
		inputDto.setIsAssociatedWithOtherDSA("NO");
		inputDto.setAssociatedInstitutionName("NA");
		inputDto.setReferralSource("NA");
		inputDto.setApprovalStatus("PENDING");

		DSAApplicationDTO outputDto = new DSAApplicationDTO();
		// Set outputDto fields similar to inputDto
		outputDto.setDsaApplicationId("DSA123");
		outputDto.setFirstName("John");
		outputDto.setMiddleName("A");
		outputDto.setLastName("Doe");
		outputDto.setGender("Male");
		outputDto.setDateOfBirth("1990-01-01");
		outputDto.setNationality("Indian");
		outputDto.setContactNumber("9873543210");
		outputDto.setEmailAddress("aa.doe@example.com");
		outputDto.setStreetAddress("123 Main St");
		outputDto.setCity("Mumbai");
		outputDto.setState("Maharashtra");
		outputDto.setPostalCode("400001");
		outputDto.setCountry("India");
		outputDto.setPreferredLanguage("English");
		outputDto.setEducationalQualifications("B.Tech");
		outputDto.setExperience("YES");
		outputDto.setIsAssociatedWithOtherDSA("NO");
		outputDto.setAssociatedInstitutionName("NA");
		outputDto.setReferralSource("NA");
		outputDto.setApprovalStatus("PENDING");
		when(dsaService.dsaApplication(any(DSAApplicationDTO.class))).thenReturn(outputDto);

		// Act
		ResponseEntity<DSAApplicationDTO> response = publicApiController.dsaApplication(inputDto);

		// Assert
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals(outputDto, response.getBody());
		verify(dsaService, times(1)).dsaApplication(inputDto);
	}

	@Test
	void testDsaApplication_ValidationFailure() throws Exception {
		// Sending empty JSON to trigger validation failure
		mockMvc.perform(post("/public/dsa-application").contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isBadRequest());
	}
}