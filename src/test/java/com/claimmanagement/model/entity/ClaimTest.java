package com.claimmanagement.model.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Claim entity
 * 
 * Tests all methods and constructors to achieve 100% coverage
 * 
 * @author Claim Management Team
 */
@DisplayName("Claim Entity Tests")
class ClaimTest {

    private Claim claim1;
    private Claim claim2;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testDateTime = LocalDateTime.of(2024, 1, 15, 14, 30, 0);
        
        claim1 = new Claim();
        claim1.setId(1L);
        claim1.setClaimNumber("CLM-2024-001");
        claim1.setPolicyNumber("POL-2024-001");
        claim1.setClaimantName("John Doe");
        claim1.setClaimantEmail("john.doe@email.com");
        claim1.setClaimantPhone("+1-555-123-4567");
        claim1.setDescription("Test claim description");
        claim1.setClaimAmount(new BigDecimal("1000.00"));
        claim1.setStatus(ClaimStatus.SUBMITTED);
        claim1.setIncidentDate(testDateTime);
        claim1.setCreatedAt(testDateTime);
        claim1.setUpdatedAt(testDateTime);

        claim2 = new Claim();
        claim2.setId(2L);
        claim2.setClaimNumber("CLM-2024-002");
        claim2.setPolicyNumber("POL-2024-002");
        claim2.setClaimantName("Jane Smith");
        claim2.setClaimantEmail("jane.smith@email.com");
        claim2.setClaimantPhone("+1-555-987-6543");
        claim2.setDescription("Another test claim");
        claim2.setClaimAmount(new BigDecimal("2000.00"));
        claim2.setStatus(ClaimStatus.UNDER_REVIEW);
        claim2.setIncidentDate(testDateTime.plusDays(1));
        claim2.setCreatedAt(testDateTime.plusDays(1));
        claim2.setUpdatedAt(testDateTime.plusDays(1));
    }

    @Test
    @DisplayName("Should create claim with default constructor")
    void shouldCreateClaimWithDefaultConstructor() {
        // When
        Claim claim = new Claim();
        
        // Then
        assertThat(claim).isNotNull();
        assertThat(claim.getId()).isNull();
        assertThat(claim.getClaimNumber()).isNull();
        assertThat(claim.getPolicyNumber()).isNull();
        assertThat(claim.getClaimantName()).isNull();
        assertThat(claim.getClaimantEmail()).isNull();
        assertThat(claim.getClaimantPhone()).isNull();
        assertThat(claim.getDescription()).isNull();
        assertThat(claim.getClaimAmount()).isNull();
        assertThat(claim.getStatus()).isNull();
        assertThat(claim.getIncidentDate()).isNull();
        assertThat(claim.getCreatedAt()).isNull();
        assertThat(claim.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("Should create claim with parameterized constructor")
    void shouldCreateClaimWithParameterizedConstructor() {
        // Given
        String claimNumber = "CLM-2024-003";
        String policyNumber = "POL-2024-003";
        String claimantName = "Test User";
        String claimantEmail = "test@email.com";
        String claimantPhone = "+1-555-000-0000";
        String description = "Constructor test claim";
        BigDecimal claimAmount = new BigDecimal("3000.00");
        ClaimStatus status = ClaimStatus.APPROVED;
        LocalDateTime incidentDate = testDateTime.plusDays(2);

        // When
        Claim claim = new Claim(claimNumber, policyNumber, claimantName, claimantEmail, 
                               claimantPhone, description, claimAmount, status, incidentDate);

        // Then
        assertThat(claim.getId()).isNull(); // Not set by constructor
        assertThat(claim.getClaimNumber()).isEqualTo(claimNumber);
        assertThat(claim.getPolicyNumber()).isEqualTo(policyNumber);
        assertThat(claim.getClaimantName()).isEqualTo(claimantName);
        assertThat(claim.getClaimantEmail()).isEqualTo(claimantEmail);
        assertThat(claim.getClaimantPhone()).isEqualTo(claimantPhone);
        assertThat(claim.getDescription()).isEqualTo(description);
        assertThat(claim.getClaimAmount()).isEqualTo(claimAmount);
        assertThat(claim.getStatus()).isEqualTo(status);
        assertThat(claim.getIncidentDate()).isEqualTo(incidentDate);
        assertThat(claim.getCreatedAt()).isNull(); // Not set by constructor
        assertThat(claim.getUpdatedAt()).isNull(); // Not set by constructor
    }

    @Test
    @DisplayName("Should set and get all properties correctly")
    void shouldSetAndGetAllPropertiesCorrectly() {
        // Given
        Claim claim = new Claim();
        Long id = 100L;
        String claimNumber = "CLM-2024-100";
        String policyNumber = "POL-2024-100";
        String claimantName = "Property Test";
        String claimantEmail = "property@test.com";
        String claimantPhone = "+1-555-100-1000";
        String description = "Property test description";
        BigDecimal claimAmount = new BigDecimal("5000.00");
        ClaimStatus status = ClaimStatus.PAID;
        LocalDateTime incidentDate = testDateTime.plusDays(5);
        LocalDateTime createdAt = testDateTime.plusDays(5);
        LocalDateTime updatedAt = testDateTime.plusDays(6);

        // When
        claim.setId(id);
        claim.setClaimNumber(claimNumber);
        claim.setPolicyNumber(policyNumber);
        claim.setClaimantName(claimantName);
        claim.setClaimantEmail(claimantEmail);
        claim.setClaimantPhone(claimantPhone);
        claim.setDescription(description);
        claim.setClaimAmount(claimAmount);
        claim.setStatus(status);
        claim.setIncidentDate(incidentDate);
        claim.setCreatedAt(createdAt);
        claim.setUpdatedAt(updatedAt);

        // Then
        assertThat(claim.getId()).isEqualTo(id);
        assertThat(claim.getClaimNumber()).isEqualTo(claimNumber);
        assertThat(claim.getPolicyNumber()).isEqualTo(policyNumber);
        assertThat(claim.getClaimantName()).isEqualTo(claimantName);
        assertThat(claim.getClaimantEmail()).isEqualTo(claimantEmail);
        assertThat(claim.getClaimantPhone()).isEqualTo(claimantPhone);
        assertThat(claim.getDescription()).isEqualTo(description);
        assertThat(claim.getClaimAmount()).isEqualTo(claimAmount);
        assertThat(claim.getStatus()).isEqualTo(status);
        assertThat(claim.getIncidentDate()).isEqualTo(incidentDate);
        assertThat(claim.getCreatedAt()).isEqualTo(createdAt);
        assertThat(claim.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    @DisplayName("Should implement equals correctly based on claim number")
    void shouldImplementEqualsCorrectlyBasedOnClaimNumber() {
        // Given
        Claim claim3 = new Claim();
        claim3.setClaimNumber("CLM-2024-001"); // Same as claim1
        claim3.setId(999L); // Different ID

        Claim claim4 = new Claim();
        claim4.setClaimNumber("CLM-2024-999"); // Different claim number

        // When & Then
        assertThat(claim1).isEqualTo(claim3); // Same claim number
        assertThat(claim1).isNotEqualTo(claim4); // Different claim number
        assertThat(claim1).isNotEqualTo(claim2); // Different claim number
        assertThat(claim1).isEqualTo(claim1); // Same object
        assertThat(claim1).isNotEqualTo(null); // Null comparison
        assertThat(claim1).isNotEqualTo("not a claim"); // Different class
    }

    @Test
    @DisplayName("Should handle equals with null claim numbers")
    void shouldHandleEqualsWithNullClaimNumbers() {
        // Given
        Claim claimWithNullNumber1 = new Claim();
        claimWithNullNumber1.setClaimNumber(null);
        
        Claim claimWithNullNumber2 = new Claim();
        claimWithNullNumber2.setClaimNumber(null);
        
        Claim claimWithNumber = new Claim();
        claimWithNumber.setClaimNumber("CLM-2024-001");

        // When & Then
        assertThat(claimWithNullNumber1).isEqualTo(claimWithNullNumber2); // Both null
        assertThat(claimWithNullNumber1).isNotEqualTo(claimWithNumber); // One null, one not
        assertThat(claimWithNumber).isNotEqualTo(claimWithNullNumber1); // One not null, one null
    }

    @Test
    @DisplayName("Should implement hashCode consistently with equals")
    void shouldImplementHashCodeConsistentlyWithEquals() {
        // Given
        Claim claim3 = new Claim();
        claim3.setClaimNumber("CLM-2024-001"); // Same as claim1

        // When & Then
        assertThat(claim1.hashCode()).isEqualTo(claim3.hashCode()); // Same claim number
        assertThat(claim1.hashCode()).isNotEqualTo(claim2.hashCode()); // Different claim number
    }

    @Test
    @DisplayName("Should handle hashCode with null claim number")
    void shouldHandleHashCodeWithNullClaimNumber() {
        // Given
        Claim claimWithNullNumber = new Claim();
        claimWithNullNumber.setClaimNumber(null);

        // When & Then
        assertThat(claimWithNullNumber.hashCode()).isEqualTo(31); // Objects.hash(null) returns 31, not 0
    }

    @Test
    @DisplayName("Should provide meaningful toString representation")
    void shouldProvideMeaningfulToStringRepresentation() {
        // When
        String toString = claim1.toString();

        // Then
        assertThat(toString).contains("Claim{");
        assertThat(toString).contains("id=" + claim1.getId());
        assertThat(toString).contains("claimNumber='" + claim1.getClaimNumber() + "'");
        assertThat(toString).contains("policyNumber='" + claim1.getPolicyNumber() + "'");
        assertThat(toString).contains("claimantName='" + claim1.getClaimantName() + "'");
        assertThat(toString).contains("claimAmount=" + claim1.getClaimAmount());
        assertThat(toString).contains("status=" + claim1.getStatus());
        assertThat(toString).contains("createdAt=" + claim1.getCreatedAt());
    }

    @Test
    @DisplayName("Should handle toString with null values")
    void shouldHandleToStringWithNullValues() {
        // Given
        Claim claimWithNulls = new Claim();

        // When
        String toString = claimWithNulls.toString();

        // Then
        assertThat(toString).contains("Claim{");
        assertThat(toString).contains("id=null");
        assertThat(toString).contains("claimNumber='null'");
        assertThat(toString).contains("policyNumber='null'");
        assertThat(toString).contains("claimantName='null'");
        assertThat(toString).contains("claimAmount=null");
        assertThat(toString).contains("status=null");
        assertThat(toString).contains("createdAt=null");
    }

    @Test
    @DisplayName("Should maintain object consistency across operations")
    void shouldMaintainObjectConsistencyAcrossOperations() {
        // Given
        Claim claim = new Claim("CLM-2024-TEST", "POL-2024-TEST", "Test User", 
                               "test@email.com", "+1-555-TEST", "Test description",
                               new BigDecimal("1000.00"), ClaimStatus.SUBMITTED, testDateTime);

        // When - Modify the claim
        claim.setId(1L);
        claim.setStatus(ClaimStatus.UNDER_REVIEW);
        claim.setCreatedAt(testDateTime);
        claim.setUpdatedAt(testDateTime.plusMinutes(30));

        // Then - Verify all properties are maintained
        assertThat(claim.getId()).isEqualTo(1L);
        assertThat(claim.getClaimNumber()).isEqualTo("CLM-2024-TEST");
        assertThat(claim.getPolicyNumber()).isEqualTo("POL-2024-TEST");
        assertThat(claim.getClaimantName()).isEqualTo("Test User");
        assertThat(claim.getClaimantEmail()).isEqualTo("test@email.com");
        assertThat(claim.getClaimantPhone()).isEqualTo("+1-555-TEST");
        assertThat(claim.getDescription()).isEqualTo("Test description");
        assertThat(claim.getClaimAmount()).isEqualTo(new BigDecimal("1000.00"));
        assertThat(claim.getStatus()).isEqualTo(ClaimStatus.UNDER_REVIEW);
        assertThat(claim.getIncidentDate()).isEqualTo(testDateTime);
        assertThat(claim.getCreatedAt()).isEqualTo(testDateTime);
        assertThat(claim.getUpdatedAt()).isEqualTo(testDateTime.plusMinutes(30));
    }
}