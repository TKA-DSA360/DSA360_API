package com.dsa360.api.controller;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.dsa360.api.dto.DSAApplicationDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class PublicApiControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

   // @Test
    void testDsaApplication_Success() throws Exception {
        // Arrange
        DSAApplicationDTO inputDto = new DSAApplicationDTO();
        inputDto.setFirstName("AAA");
        inputDto.setMiddleName("A");
        inputDto.setLastName("CCC");
        inputDto.setGender("Male");
        inputDto.setDateOfBirth("1990-01-01");
        inputDto.setNationality("Indian");
        inputDto.setContactNumber("8076543210");
        inputDto.setEmailAddress("aa.ccc@example.com");
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

        // Act & Assert
        mockMvc.perform(post("/public/dsa-application")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.contactNumber").value("8876543210"))
                .andExpect(jsonPath("$.firstName").value("AAA"));
    }

    @Test
    void testDsaApplication_ValidationFailure() throws Exception {
        // Arrange
        DSAApplicationDTO inputDto = new DSAApplicationDTO();
        // Missing mandatory fields

        // Act & Assert
        mockMvc.perform(post("/public/dsa-application")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isBadRequest());
    }
}