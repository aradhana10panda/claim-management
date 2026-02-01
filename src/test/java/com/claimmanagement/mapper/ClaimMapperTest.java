package com.claimmanagement.mapper;

import com.claimmanagement.model.dto.ClaimRequestDto;
import com.claimmanagement.model.dto.ClaimResponseDto;
import com.claimmanagement.model.entity.Claim;
import com.claimmanagement.model.entity.ClaimStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ClaimMapper
 * 
 * Tests all mapping methods to achieve 100% coverage
 * 
 * @author Claim Management Team
 */
@DisplayName("ClaimMapper Tests")
class ClaimMapperTest {

    private ClaimMapper claimMapper;
    private ClaimRequestDto requestDto;
    private Claim claim;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        claimMapper = Mappers.getMapper(ClaimMapper.class);
        testDateTime = LocalDateTime.of(2024, 1, 15, 14, 30, 0);

        // Setup test request DTO
        requestDto = new ClaimRequestDto();
        requestDto.setPolicyNumber("POL-2024-001");
        requestDto.setClaimantName("John Doe");
        requestDto.setClaimantEmail("john.doe@email.com");
        requestDto.setClaimantPhone("+1-555-123-4567");
        requestDto.setDescription("Test claim description");
        requestDto.setClaimAmount(new BigDecimal("1000.00"));
        requestDto.setStatus(ClaimStatus.SUBMITTED);
        requestDto.setIncidentDate(testDateTime);

        // Setup test entity
        claim = new Claim();
        claim.setId(1L);
        claim.setClaimNumber("CLM-2024-001");
        claim.setPolicyNumber("POL-2024-001");
        claim.setClaimantName("John Doe");
        claim.setClaimantEmail("john.doe@email.com");
        claim.setClaimantPhone("+1-555-123-4567");
        claim.setDescription("Test claim description");
        claim.setClaimAmount(new BigDecimal("1000.00"));
        claim.setStatus(ClaimStatus.SUBMITTED);
        claim.setIncidentDate(testDateTime);
        claim.setCreatedAt(testDateTime);
        claim.setUpdatedAt(testDateTime);
    }

    @Test
    @DisplayName("Should map ClaimRequestDto to Claim entity")
    void shouldMapClaimRequestDtoToClaimEntity() {
        // When
        Claim mappedClaim = claimMapper.toEntity(requestDto);

        // Then
        assertThat(mappedClaim).isNotNull();
        assertThat(mappedClaim.getId()).isNull(); // Should be ignored
        assertThat(mappedClaim.getClaimNumber()).isNull(); // Should be ignored
        assertThat(mappedClaim.getPolicyNumber()).isEqualTo(requestDto.getPolicyNumber());
        assertThat(mappedClaim.getClaimantName()).isEqualTo(requestDto.getClaimantName());
        assertThat(mappedClaim.getClaimantEmail()).isEqualTo(requestDto.getClaimantEmail());
        assertThat(mappedClaim.getClaimantPhone()).isEqualTo(requestDto.getClaimantPhone());
        assertThat(mappedClaim.getDescription()).isEqualTo(requestDto.getDescription());
        assertThat(mappedClaim.getClaimAmount()).isEqualTo(requestDto.getClaimAmount());
        assertThat(mappedClaim.getStatus()).isEqualTo(requestDto.getStatus());
        assertThat(mappedClaim.getIncidentDate()).isEqualTo(requestDto.getIncidentDate());
        assertThat(mappedClaim.getCreatedAt()).isNull(); // Should be ignored
        assertThat(mappedClaim.getUpdatedAt()).isNull(); // Should be ignored
    }

    @Test
    @DisplayName("Should map Claim entity to ClaimResponseDto")
    void shouldMapClaimEntityToClaimResponseDto() {
        // When
        ClaimResponseDto responseDto = claimMapper.toResponseDto(claim);

        // Then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getId()).isEqualTo(claim.getId());
        assertThat(responseDto.getClaimNumber()).isEqualTo(claim.getClaimNumber());
        assertThat(responseDto.getPolicyNumber()).isEqualTo(claim.getPolicyNumber());
        assertThat(responseDto.getClaimantName()).isEqualTo(claim.getClaimantName());
        assertThat(responseDto.getClaimantEmail()).isEqualTo(claim.getClaimantEmail());
        assertThat(responseDto.getClaimantPhone()).isEqualTo(claim.getClaimantPhone());
        assertThat(responseDto.getDescription()).isEqualTo(claim.getDescription());
        assertThat(responseDto.getClaimAmount()).isEqualTo(claim.getClaimAmount());
        assertThat(responseDto.getStatus()).isEqualTo(claim.getStatus());
        assertThat(responseDto.getIncidentDate()).isEqualTo(claim.getIncidentDate());
        assertThat(responseDto.getCreatedAt()).isEqualTo(claim.getCreatedAt());
        assertThat(responseDto.getUpdatedAt()).isEqualTo(claim.getUpdatedAt());
    }

    @Test
    @DisplayName("Should map list of Claim entities to list of ClaimResponseDtos")
    void shouldMapListOfClaimEntitiesToListOfClaimResponseDtos() {
        // Given
        Claim claim2 = new Claim();
        claim2.setId(2L);
        claim2.setClaimNumber("CLM-2024-002");
        claim2.setPolicyNumber("POL-2024-002");
        claim2.setClaimantName("Jane Smith");
        claim2.setClaimantEmail("jane.smith@email.com");
        claim2.setClaimAmount(new BigDecimal("2000.00"));
        claim2.setStatus(ClaimStatus.UNDER_REVIEW);
        claim2.setIncidentDate(testDateTime.plusDays(1));
        claim2.setCreatedAt(testDateTime.plusDays(1));
        claim2.setUpdatedAt(testDateTime.plusDays(1));

        List<Claim> claims = Arrays.asList(claim, claim2);

        // When
        List<ClaimResponseDto> responseDtos = claimMapper.toResponseDtoList(claims);

        // Then
        assertThat(responseDtos).isNotNull();
        assertThat(responseDtos).hasSize(2);
        
        ClaimResponseDto firstDto = responseDtos.get(0);
        assertThat(firstDto.getId()).isEqualTo(claim.getId());
        assertThat(firstDto.getClaimNumber()).isEqualTo(claim.getClaimNumber());
        assertThat(firstDto.getClaimantName()).isEqualTo(claim.getClaimantName());
        
        ClaimResponseDto secondDto = responseDtos.get(1);
        assertThat(secondDto.getId()).isEqualTo(claim2.getId());
        assertThat(secondDto.getClaimNumber()).isEqualTo(claim2.getClaimNumber());
        assertThat(secondDto.getClaimantName()).isEqualTo(claim2.getClaimantName());
    }

    @Test
    @DisplayName("Should update existing Claim entity from ClaimRequestDto")
    void shouldUpdateExistingClaimEntityFromClaimRequestDto() {
        // Given
        Claim existingClaim = new Claim();
        existingClaim.setId(1L);
        existingClaim.setClaimNumber("CLM-2024-001");
        existingClaim.setPolicyNumber("OLD-POLICY");
        existingClaim.setClaimantName("Old Name");
        existingClaim.setClaimantEmail("old@email.com");
        existingClaim.setClaimAmount(new BigDecimal("500.00"));
        existingClaim.setStatus(ClaimStatus.SUBMITTED);
        existingClaim.setCreatedAt(testDateTime.minusDays(1));
        existingClaim.setUpdatedAt(testDateTime.minusDays(1));

        // When
        claimMapper.updateEntityFromDto(requestDto, existingClaim);

        // Then
        assertThat(existingClaim.getId()).isEqualTo(1L); // Should not change
        assertThat(existingClaim.getClaimNumber()).isEqualTo("CLM-2024-001"); // Should not change
        assertThat(existingClaim.getPolicyNumber()).isEqualTo(requestDto.getPolicyNumber()); // Should update
        assertThat(existingClaim.getClaimantName()).isEqualTo(requestDto.getClaimantName()); // Should update
        assertThat(existingClaim.getClaimantEmail()).isEqualTo(requestDto.getClaimantEmail()); // Should update
        assertThat(existingClaim.getClaimantPhone()).isEqualTo(requestDto.getClaimantPhone()); // Should update
        assertThat(existingClaim.getDescription()).isEqualTo(requestDto.getDescription()); // Should update
        assertThat(existingClaim.getClaimAmount()).isEqualTo(requestDto.getClaimAmount()); // Should update
        assertThat(existingClaim.getStatus()).isEqualTo(requestDto.getStatus()); // Should update
        assertThat(existingClaim.getIncidentDate()).isEqualTo(requestDto.getIncidentDate()); // Should update
        assertThat(existingClaim.getCreatedAt()).isEqualTo(testDateTime.minusDays(1)); // Should not change
        assertThat(existingClaim.getUpdatedAt()).isEqualTo(testDateTime.minusDays(1)); // Should not change
    }

    @Test
    @DisplayName("Should create status response DTO with ignored fields")
    void shouldCreateStatusResponseDtoWithIgnoredFields() {
        // When
        ClaimResponseDto statusResponseDto = claimMapper.toStatusResponseDto(claim);

        // Then
        assertThat(statusResponseDto).isNotNull();
        assertThat(statusResponseDto.getId()).isEqualTo(claim.getId());
        assertThat(statusResponseDto.getClaimNumber()).isEqualTo(claim.getClaimNumber());
        assertThat(statusResponseDto.getPolicyNumber()).isEqualTo(claim.getPolicyNumber());
        assertThat(statusResponseDto.getClaimantName()).isEqualTo(claim.getClaimantName());
        assertThat(statusResponseDto.getClaimantEmail()).isEqualTo(claim.getClaimantEmail());
        assertThat(statusResponseDto.getClaimantPhone()).isNull(); // Should be ignored
        assertThat(statusResponseDto.getDescription()).isNull(); // Should be ignored
        assertThat(statusResponseDto.getClaimAmount()).isEqualTo(claim.getClaimAmount());
        assertThat(statusResponseDto.getStatus()).isEqualTo(claim.getStatus());
        assertThat(statusResponseDto.getIncidentDate()).isEqualTo(claim.getIncidentDate());
        assertThat(statusResponseDto.getCreatedAt()).isEqualTo(claim.getCreatedAt());
        assertThat(statusResponseDto.getUpdatedAt()).isEqualTo(claim.getUpdatedAt());
    }

    @Test
    @DisplayName("Should handle null values in mapping")
    void shouldHandleNullValuesInMapping() {
        // Given
        ClaimRequestDto nullRequestDto = null;
        Claim nullClaim = null;

        // When & Then
        assertThat(claimMapper.toEntity(nullRequestDto)).isNull();
        assertThat(claimMapper.toResponseDto(nullClaim)).isNull();
        assertThat(claimMapper.toResponseDtoList(null)).isNull();
        assertThat(claimMapper.toStatusResponseDto(nullClaim)).isNull();
    }

    @Test
    @DisplayName("Should handle empty list in mapping")
    void shouldHandleEmptyListInMapping() {
        // Given
        List<Claim> emptyClaims = Arrays.asList();

        // When
        List<ClaimResponseDto> responseDtos = claimMapper.toResponseDtoList(emptyClaims);

        // Then
        assertThat(responseDtos).isNotNull();
        assertThat(responseDtos).isEmpty();
    }

    @Test
    @DisplayName("Should handle partial data in request DTO")
    void shouldHandlePartialDataInRequestDto() {
        // Given
        ClaimRequestDto partialDto = new ClaimRequestDto();
        partialDto.setPolicyNumber("POL-PARTIAL");
        partialDto.setClaimantName("Partial User");
        partialDto.setClaimantEmail("partial@email.com");
        // Leave other fields null

        // When
        Claim mappedClaim = claimMapper.toEntity(partialDto);

        // Then
        assertThat(mappedClaim).isNotNull();
        assertThat(mappedClaim.getPolicyNumber()).isEqualTo("POL-PARTIAL");
        assertThat(mappedClaim.getClaimantName()).isEqualTo("Partial User");
        assertThat(mappedClaim.getClaimantEmail()).isEqualTo("partial@email.com");
        assertThat(mappedClaim.getClaimantPhone()).isNull();
        assertThat(mappedClaim.getDescription()).isNull();
        assertThat(mappedClaim.getClaimAmount()).isNull();
        assertThat(mappedClaim.getStatus()).isEqualTo(ClaimStatus.SUBMITTED); // Set by @AfterMapping
        assertThat(mappedClaim.getIncidentDate()).isNull();
    }

    @Test
    @DisplayName("Should handle partial data in entity")
    void shouldHandlePartialDataInEntity() {
        // Given
        Claim partialClaim = new Claim();
        partialClaim.setId(1L);
        partialClaim.setClaimNumber("CLM-PARTIAL");
        partialClaim.setPolicyNumber("POL-PARTIAL");
        // Leave other fields null

        // When
        ClaimResponseDto responseDto = claimMapper.toResponseDto(partialClaim);

        // Then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getId()).isEqualTo(1L);
        assertThat(responseDto.getClaimNumber()).isEqualTo("CLM-PARTIAL");
        assertThat(responseDto.getPolicyNumber()).isEqualTo("POL-PARTIAL");
        assertThat(responseDto.getClaimantName()).isNull();
        assertThat(responseDto.getClaimantEmail()).isNull();
        assertThat(responseDto.getClaimantPhone()).isNull();
        assertThat(responseDto.getDescription()).isNull();
        assertThat(responseDto.getClaimAmount()).isNull();
        assertThat(responseDto.getStatus()).isNull();
        assertThat(responseDto.getIncidentDate()).isNull();
        assertThat(responseDto.getCreatedAt()).isNull();
        assertThat(responseDto.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("Should preserve precision in BigDecimal mapping")
    void shouldPreservePrecisionInBigDecimalMapping() {
        // Given
        BigDecimal preciseAmount = new BigDecimal("1234.56789");
        requestDto.setClaimAmount(preciseAmount);

        // When
        Claim mappedClaim = claimMapper.toEntity(requestDto);

        // Then
        assertThat(mappedClaim.getClaimAmount()).isEqualTo(preciseAmount);
        assertThat(mappedClaim.getClaimAmount().scale()).isEqualTo(preciseAmount.scale());
    }

    @Test
    @DisplayName("Should handle different ClaimStatus values")
    void shouldHandleDifferentClaimStatusValues() {
        // Test all status values
        ClaimStatus[] allStatuses = ClaimStatus.values();
        
        for (ClaimStatus status : allStatuses) {
            // Given
            requestDto.setStatus(status);
            
            // When
            Claim mappedClaim = claimMapper.toEntity(requestDto);
            
            // Then
            assertThat(mappedClaim.getStatus()).isEqualTo(status);
        }
    }
}