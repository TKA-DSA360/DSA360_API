package com.dsa360.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.dsa360.api.dao.DSADao;
import com.dsa360.api.dto.DSAApplicationDTO;
import com.dsa360.api.entity.DsaApplicationEntity;

@SpringBootTest
public class DSAServiceTest {

	@Autowired
	private DSAService dsaService;

	@MockBean
	private DSADao dsaRepository;

	@Test
	void testDsaApplication_Success() {
		DSAApplicationDTO inputDto = new DSAApplicationDTO();
	    inputDto.setFirstName("Ram");
	    inputDto.setMiddleName("A");
	    inputDto.setLastName("Doe");
	    inputDto.setGender("Male");
	    inputDto.setDateOfBirth("1990-01-01");
	    inputDto.setNationality("Indian");
	    inputDto.setContactNumber("9876543210");
	    inputDto.setEmailAddress("john.doe@example.com");
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
	    inputDto.setEmailVerified(false);

		when(dsaRepository.dsaApplication(any())).thenReturn(new DsaApplicationEntity());

		DSAApplicationDTO result = dsaService.dsaApplication(inputDto);

		assertNotNull(result);
		assertEquals("9876543210", result.getContactNumber());
		verify(dsaRepository, times(1)).dsaApplication(any());
	}
}