package com.dsa360.api.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.validation.Validator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DSAApplicationDTOValidationTest {

	@Autowired
	private Validator validator;

	@Test
	void testValidation_ValidDTO() {
		DSAApplicationDTO dto = new DSAApplicationDTO();
		dto.setFirstName("John");
		dto.setMiddleName("A");
		dto.setLastName("Doe");
		dto.setGender("Male");
		dto.setDateOfBirth("1990-01-01");
		dto.setNationality("Indian");
		dto.setContactNumber("9876543210");
		dto.setEmailAddress("john.doe@example.com");
		dto.setStreetAddress("123 Main St");
		dto.setCity("Mumbai");
		dto.setState("Maharashtra");
		dto.setPostalCode("400001");
		dto.setCountry("India");
		dto.setPreferredLanguage("English");
		dto.setEducationalQualifications("B.Tech");
		dto.setExperience("YES");
		dto.setIsAssociatedWithOtherDSA("NO");
		dto.setAssociatedInstitutionName("NA");
		dto.setReferralSource("NA");

		var violations = validator.validate(dto);
		assertTrue(violations.isEmpty(), "Valid DTO should have no violations");
	}

	@Test
	void testValidation_InvalidEmail() {
		DSAApplicationDTO dto = new DSAApplicationDTO();
		// Set all fields correctly except email
		dto.setFirstName("John");
		dto.setMiddleName("A");
		dto.setLastName("Doe");
		dto.setGender("Male");
		dto.setDateOfBirth("1990-01-01");
		dto.setNationality("Indian");
		dto.setContactNumber("9876543210");
		dto.setEmailAddress("invalid-email"); // Invalid email
		dto.setStreetAddress("123 Main St");
		dto.setCity("Mumbai");
		dto.setState("Maharashtra");
		dto.setPostalCode("400001");
		dto.setCountry("India");
		dto.setPreferredLanguage("English");
		dto.setEducationalQualifications("B.Tech");
		dto.setExperience("YES");
		dto.setIsAssociatedWithOtherDSA("NO");
		dto.setAssociatedInstitutionName("NA");
		dto.setReferralSource("NA");

		var violations = validator.validate(dto);
		assertFalse(violations.isEmpty(), "Invalid email should cause a violation");
		assertEquals(1, violations.size());
		assertEquals("Email should be valid", violations.iterator().next().getMessage());
	}
}