package com.claimmanagement.repository;

import com.claimmanagement.model.entity.Claim;
import com.claimmanagement.model.entity.ClaimStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository layer tests for ClaimRepository
 * 
 * @DataJpaTest provides:
 * 1. Configures in-memory database (H2)
 * 2. Configures Spring Data JPA repositories
 * 3. Provides TestEntityManager for test data setup
 * 4. Rolls back transactions after each test
 * 5. Only loads JPA-related components (not full application context)
 * 
 * TestEntityManager:
 * - Provides methods to persist, flush, and find entities
 * - Alternative to using repository for test data setup
 * - Useful for testing custom queries
 * 
 * Testing Strategy:
 * - Test custom query methods
 * - Verify JPA relationships and mappings
 * - Test pagination and sorting
 * - Verify database constraints
 * 
 * @author Claim Management Team
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Claim Repository Tests")
class ClaimRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ClaimRepository claimRepository;

    private Claim testClaim1;
    private Claim testClaim2;
    private Claim testClaim3;

    @BeforeEach
    void setUp() {
        // Create test claims with different attributes
        testClaim1 = createTestClaim(
            "CLM-2024-001",
            "POL-AUTO-001",
            "John Doe",
            "john.doe@email.com",
            "+1-555-001-0001",
            "Auto accident claim",
            new BigDecimal("2500.00"),
            ClaimStatus.SUBMITTED,
            LocalDateTime.now().minusDays(5)
        );

        testClaim2 = createTestClaim(
            "CLM-2024-002",
            "POL-HOME-002",
            "Jane Smith",
            "jane.smith@email.com",
            "+1-555-002-0002",
            "Home insurance claim",
            new BigDecimal("8500.00"),
            ClaimStatus.UNDER_REVIEW,
            LocalDateTime.now().minusDays(3)
        );

        testClaim3 = createTestClaim(
            "CLM-2024-003",
            "POL-AUTO-001", // Same policy as testClaim1
            "Bob Johnson",
            "bob.johnson@email.com",
            "+1-555-003-0003",
            "Another auto claim",
            new BigDecimal("1200.00"),
            ClaimStatus.APPROVED,
            LocalDateTime.now().minusDays(1)
        );

        // Persist test data
        entityManager.persistAndFlush(testClaim1);
        entityManager.persistAndFlush(testClaim2);
        entityManager.persistAndFlush(testClaim3);
    }

    /**
     * Helper method to create test claims
     */
    private Claim createTestClaim(String claimNumber, String policyNumber, String claimantName,
                                 String claimantEmail, String claimantPhone, String description,
                                 BigDecimal claimAmount, ClaimStatus status, LocalDateTime incidentDate) {
        Claim claim = new Claim();
        claim.setClaimNumber(claimNumber);
        claim.setPolicyNumber(policyNumber);
        claim.setClaimantName(claimantName);
        claim.setClaimantEmail(claimantEmail);
        claim.setClaimantPhone(claimantPhone);
        claim.setDescription(description);
        claim.setClaimAmount(claimAmount);
        claim.setStatus(status);
        claim.setIncidentDate(incidentDate);
        return claim;
    }

    @Test
    @DisplayName("Should find claim by claim number")
    void shouldFindClaimByClaimNumber() {
        // When
        Optional<Claim> found = claimRepository.findByClaimNumber("CLM-2024-001");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getClaimantName()).isEqualTo("John Doe");
        assertThat(found.get().getPolicyNumber()).isEqualTo("POL-AUTO-001");
    }

    @Test
    @DisplayName("Should return empty when claim number not found")
    void shouldReturnEmptyWhenClaimNumberNotFound() {
        // When
        Optional<Claim> found = claimRepository.findByClaimNumber("NON-EXISTENT");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should check if claim number exists")
    void shouldCheckIfClaimNumberExists() {
        // When & Then
        assertThat(claimRepository.existsByClaimNumber("CLM-2024-001")).isTrue();
        assertThat(claimRepository.existsByClaimNumber("NON-EXISTENT")).isFalse();
    }

    @Test
    @DisplayName("Should find claims by policy number")
    void shouldFindClaimsByPolicyNumber() {
        // When
        List<Claim> claims = claimRepository.findByPolicyNumber("POL-AUTO-001");

        // Then
        assertThat(claims).hasSize(2);
        assertThat(claims).extracting(Claim::getClaimantName)
                         .containsExactlyInAnyOrder("John Doe", "Bob Johnson");
    }

    @Test
    @DisplayName("Should find claims by status")
    void shouldFindClaimsByStatus() {
        // When
        List<Claim> submittedClaims = claimRepository.findByStatus(ClaimStatus.SUBMITTED);

        // Then
        assertThat(submittedClaims).hasSize(1);
        assertThat(submittedClaims.get(0).getClaimantName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Should find claims by claimant email")
    void shouldFindClaimsByClaimantEmail() {
        // When
        List<Claim> claims = claimRepository.findByClaimantEmail("jane.smith@email.com");

        // Then
        assertThat(claims).hasSize(1);
        assertThat(claims.get(0).getClaimantName()).isEqualTo("Jane Smith");
    }

    @Test
    @DisplayName("Should find claims by amount greater than threshold")
    void shouldFindClaimsByAmountGreaterThan() {
        // When
        List<Claim> highValueClaims = claimRepository.findByClaimAmountGreaterThan(new BigDecimal("5000"));

        // Then
        assertThat(highValueClaims).hasSize(1);
        assertThat(highValueClaims.get(0).getClaimantName()).isEqualTo("Jane Smith");
        assertThat(highValueClaims.get(0).getClaimAmount()).isEqualTo(new BigDecimal("8500.00"));
    }

    @Test
    @DisplayName("Should find claims created between dates")
    void shouldFindClaimsCreatedBetweenDates() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(4);
        LocalDateTime endDate = LocalDateTime.now().minusDays(2);

        // When
        List<Claim> claims = claimRepository.findByCreatedAtBetween(startDate, endDate);

        // Then
        assertThat(claims).hasSize(1);
        assertThat(claims.get(0).getClaimantName()).isEqualTo("Jane Smith");
    }

    @Test
    @DisplayName("Should find claims by claimant name containing (case insensitive)")
    void shouldFindClaimsByClaimantNameContaining() {
        // When
        List<Claim> claims = claimRepository.findByClaimantNameContainingIgnoreCase("john");

        // Then
        assertThat(claims).hasSize(2);
        assertThat(claims).extracting(Claim::getClaimantName)
                         .containsExactlyInAnyOrder("John Doe", "Bob Johnson");
    }

    @Test
    @DisplayName("Should find claims with pagination")
    void shouldFindClaimsWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 2);

        // When
        Page<Claim> claimsPage = claimRepository.findAll(pageable);

        // Then
        assertThat(claimsPage.getContent()).hasSize(2);
        assertThat(claimsPage.getTotalElements()).isEqualTo(3);
        assertThat(claimsPage.getTotalPages()).isEqualTo(2);
        assertThat(claimsPage.getNumber()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should find claims by status with pagination")
    void shouldFindClaimsByStatusWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Claim> submittedClaims = claimRepository.findByStatus(ClaimStatus.SUBMITTED, pageable);

        // Then
        assertThat(submittedClaims.getContent()).hasSize(1);
        assertThat(submittedClaims.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should find claims by policy and status")
    void shouldFindClaimsByPolicyAndStatus() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Claim> claims = claimRepository.findByPolicyNumberAndStatus(
            "POL-AUTO-001", ClaimStatus.SUBMITTED, pageable);

        // Then
        assertThat(claims.getContent()).hasSize(1);
        assertThat(claims.getContent().get(0).getClaimantName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Should find claims by optional criteria")
    void shouldFindClaimsByOptionalCriteria() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When - Search with policy number only
        Page<Claim> claimsByPolicy = claimRepository.findByOptionalCriteria(
            "POL-AUTO-001", null, null, pageable);

        // Then
        assertThat(claimsByPolicy.getContent()).hasSize(2);

        // When - Search with status only
        Page<Claim> claimsByStatus = claimRepository.findByOptionalCriteria(
            null, ClaimStatus.UNDER_REVIEW, null, pageable);

        // Then
        assertThat(claimsByStatus.getContent()).hasSize(1);
        assertThat(claimsByStatus.getContent().get(0).getClaimantName()).isEqualTo("Jane Smith");

        // When - Search with email only
        Page<Claim> claimsByEmail = claimRepository.findByOptionalCriteria(
            null, null, "bob.johnson@email.com", pageable);

        // Then
        assertThat(claimsByEmail.getContent()).hasSize(1);
        assertThat(claimsByEmail.getContent().get(0).getClaimantName()).isEqualTo("Bob Johnson");

        // When - Search with multiple criteria
        Page<Claim> claimsByMultiple = claimRepository.findByOptionalCriteria(
            "POL-AUTO-001", ClaimStatus.APPROVED, null, pageable);

        // Then
        assertThat(claimsByMultiple.getContent()).hasSize(1);
        assertThat(claimsByMultiple.getContent().get(0).getClaimantName()).isEqualTo("Bob Johnson");
    }

    @Test
    @DisplayName("Should count claims by status")
    void shouldCountClaimsByStatus() {
        // When & Then
        assertThat(claimRepository.countByStatus(ClaimStatus.SUBMITTED)).isEqualTo(1);
        assertThat(claimRepository.countByStatus(ClaimStatus.UNDER_REVIEW)).isEqualTo(1);
        assertThat(claimRepository.countByStatus(ClaimStatus.APPROVED)).isEqualTo(1);
        assertThat(claimRepository.countByStatus(ClaimStatus.PAID)).isEqualTo(0);
    }

    @Test
    @DisplayName("Should get claim statistics by status")
    void shouldGetClaimStatisticsByStatus() {
        // When
        Object[] stats = claimRepository.getClaimStatisticsByStatus(ClaimStatus.SUBMITTED);

        // Then
        assertThat(stats).hasSize(3);
        assertThat(stats[0]).isEqualTo(1L); // Count
        assertThat(stats[1]).isEqualTo(new BigDecimal("2500.00")); // Sum
        assertThat(stats[2]).isEqualTo(new BigDecimal("2500.00")); // Average
    }

    @Test
    @DisplayName("Should delete claims by policy number")
    void shouldDeleteClaimsByPolicyNumber() {
        // Given
        assertThat(claimRepository.findByPolicyNumber("POL-AUTO-001")).hasSize(2);

        // When
        long deletedCount = claimRepository.deleteByPolicyNumber("POL-AUTO-001");

        // Then
        assertThat(deletedCount).isEqualTo(2);
        assertThat(claimRepository.findByPolicyNumber("POL-AUTO-001")).isEmpty();
    }

    @Test
    @DisplayName("Should handle entity relationships and constraints")
    void shouldHandleEntityRelationshipsAndConstraints() {
        // Test unique constraint on claim number
        Claim duplicateClaimNumber = createTestClaim(
            "CLM-2024-001", // Duplicate claim number
            "POL-TEST-999",
            "Test User",
            "test@email.com",
            "+1-555-999-9999",
            "Test description",
            new BigDecimal("1000.00"),
            ClaimStatus.SUBMITTED,
            LocalDateTime.now()
        );

        // This should fail due to unique constraint
        try {
            entityManager.persistAndFlush(duplicateClaimNumber);
            // If we reach here, the test should fail
            assertThat(false).as("Expected constraint violation").isTrue();
        } catch (Exception e) {
            // Expected behavior - constraint violation
            assertThat(e).isNotNull();
        }
    }

    @Test
    @DisplayName("Should handle timestamp fields correctly")
    void shouldHandleTimestampFieldsCorrectly() {
        // Given
        Claim newClaim = createTestClaim(
            "CLM-2024-999",
            "POL-TEST-999",
            "Timestamp Test",
            "timestamp@email.com",
            "+1-555-999-9999",
            "Timestamp test claim",
            new BigDecimal("1000.00"),
            ClaimStatus.SUBMITTED,
            LocalDateTime.now()
        );

        // When
        Claim savedClaim = claimRepository.save(newClaim);

        // Then
        assertThat(savedClaim.getCreatedAt()).isNotNull();
        assertThat(savedClaim.getUpdatedAt()).isNotNull();
        assertThat(savedClaim.getCreatedAt()).isEqualTo(savedClaim.getUpdatedAt());

        // Update the claim
        savedClaim.setDescription("Updated description");
        Claim updatedClaim = claimRepository.save(savedClaim);

        // Verify updatedAt changed
        assertThat(updatedClaim.getUpdatedAt()).isAfter(updatedClaim.getCreatedAt());
    }
}