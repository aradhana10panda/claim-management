package com.claimmanagement.service;

import com.claimmanagement.exception.ClaimNotFoundException;
import com.claimmanagement.exception.InvalidClaimStateException;
import com.claimmanagement.mapper.ClaimMapper;
import com.claimmanagement.model.dto.ClaimRequestDto;
import com.claimmanagement.model.dto.ClaimResponseDto;
import com.claimmanagement.model.entity.Claim;
import com.claimmanagement.model.entity.ClaimStatus;
import com.claimmanagement.repository.ClaimRepository;
import com.claimmanagement.service.impl.ClaimServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ClaimService
 * 
 * Unit Testing Best Practices:
 * 1. Test one unit of work in isolation
 * 2. Mock external dependencies
 * 3. Use descriptive test names
 * 4. Follow AAA pattern (Arrange, Act, Assert)
 * 5. Test both happy path and edge cases
 * 6. Verify interactions with mocks
 * 
 * JUnit 5 Annotations:
 * @ExtendWith(MockitoExtension.class) - Enables Mockito annotations
 * @Mock - Creates mock objects
 * @InjectMocks - Injects mocks into the tested object
 * @BeforeEach - Runs before each test method
 * @Test - Marks test methods
 * @DisplayName - Provides readable test names
 * 
 * Mockito Features:
 * - when().thenReturn() - Stub method calls
 * - verify() - Verify method calls
 * - ArgumentMatchers - Flexible argument matching
 * - @Mock - Create mock objects
 * - @InjectMocks - Inject mocks into test subject
 * 
 * @author Claim Management Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Claim Service Tests")
class ClaimServiceTest {

    /**
     * Mock repository - simulates database operations
     */
    @Mock
    private ClaimRepository claimRepository;

    /**
     * Mock mapper - simulates entity-DTO conversions
     */
    @Mock
    private ClaimMapper claimMapper;

    /**
     * Service under test - real instance with mocked dependencies
     */
    @InjectMocks
    private ClaimServiceImpl claimService;

    /**
     * Test data objects
     */
    private ClaimRequestDto requestDto;
    private Claim claim;
    private ClaimResponseDto responseDto;

    /**
     * Set up test data before each test
     * 
     * @BeforeEach ensures fresh test data for each test method
     * This prevents test pollution and ensures test isolation
     */
    @BeforeEach
    void setUp() {
        // Create test request DTO
        requestDto = new ClaimRequestDto();
        requestDto.setPolicyNumber("POL-2024-001");
        requestDto.setClaimantName("John Doe");
        requestDto.setClaimantEmail("john.doe@email.com");
        requestDto.setClaimantPhone("+1-555-123-4567");
        requestDto.setDescription("Test claim description");
        requestDto.setClaimAmount(new BigDecimal("1000.00"));
        requestDto.setStatus(ClaimStatus.SUBMITTED);
        requestDto.setIncidentDate(LocalDateTime.now().minusDays(1));

        // Create test entity
        claim = new Claim();
        claim.setId(1L);
        claim.setClaimNumber("CLM-2024-000001");
        claim.setPolicyNumber("POL-2024-001");
        claim.setClaimantName("John Doe");
        claim.setClaimantEmail("john.doe@email.com");
        claim.setClaimantPhone("+1-555-123-4567");
        claim.setDescription("Test claim description");
        claim.setClaimAmount(new BigDecimal("1000.00"));
        claim.setStatus(ClaimStatus.SUBMITTED);
        claim.setIncidentDate(LocalDateTime.now().minusDays(1));
        claim.setCreatedAt(LocalDateTime.now());
        claim.setUpdatedAt(LocalDateTime.now());

        // Create test response DTO
        responseDto = new ClaimResponseDto();
        responseDto.setId(1L);
        responseDto.setClaimNumber("CLM-2024-000001");
        responseDto.setPolicyNumber("POL-2024-001");
        responseDto.setClaimantName("John Doe");
        responseDto.setClaimantEmail("john.doe@email.com");
        responseDto.setClaimantPhone("+1-555-123-4567");
        responseDto.setDescription("Test claim description");
        responseDto.setClaimAmount(new BigDecimal("1000.00"));
        responseDto.setStatus(ClaimStatus.SUBMITTED);
        responseDto.setIncidentDate(LocalDateTime.now().minusDays(1));
        responseDto.setCreatedAt(LocalDateTime.now());
        responseDto.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * Test successful claim creation
     * 
     * Tests the happy path for creating a new claim
     * Verifies all interactions and return value
     */
    @Test
    @DisplayName("Should create claim successfully")
    void shouldCreateClaimSuccessfully() {
        // Arrange - Set up mock behavior
        when(claimMapper.toEntity(requestDto)).thenReturn(claim);
        when(claimRepository.existsByClaimNumber(anyString())).thenReturn(false);
        when(claimRepository.save(any(Claim.class))).thenReturn(claim);
        when(claimMapper.toResponseDto(claim)).thenReturn(responseDto);

        // Act - Execute the method under test
        ClaimResponseDto result = claimService.createClaim(requestDto);

        // Assert - Verify the results
        assertNotNull(result);
        assertEquals(responseDto.getId(), result.getId());
        assertEquals(responseDto.getClaimNumber(), result.getClaimNumber());
        assertEquals(responseDto.getPolicyNumber(), result.getPolicyNumber());

        // Verify interactions with mocks
        verify(claimMapper).toEntity(requestDto);
        verify(claimRepository).save(any(Claim.class));
        verify(claimMapper).toResponseDto(claim);
    }

    /**
     * Test claim creation with invalid amount
     * 
     * Tests business rule validation during claim creation
     */
    @Test
    @DisplayName("Should throw exception for invalid claim amount")
    void shouldThrowExceptionForInvalidClaimAmount() {
        // Arrange
        requestDto.setClaimAmount(BigDecimal.ZERO);
        when(claimMapper.toEntity(requestDto)).thenReturn(claim);
        claim.setClaimAmount(BigDecimal.ZERO);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            claimService.createClaim(requestDto);
        });

        // Verify that save was not called due to validation failure
        verify(claimRepository, never()).save(any(Claim.class));
    }

    /**
     * Test successful claim retrieval by ID
     */
    @Test
    @DisplayName("Should retrieve claim by ID successfully")
    void shouldRetrieveClaimByIdSuccessfully() {
        // Arrange
        Long claimId = 1L;
        when(claimRepository.findById(claimId)).thenReturn(Optional.of(claim));
        when(claimMapper.toResponseDto(claim)).thenReturn(responseDto);

        // Act
        ClaimResponseDto result = claimService.getClaimById(claimId);

        // Assert
        assertNotNull(result);
        assertEquals(responseDto.getId(), result.getId());
        assertEquals(responseDto.getClaimNumber(), result.getClaimNumber());

        // Verify interactions
        verify(claimRepository).findById(claimId);
        verify(claimMapper).toResponseDto(claim);
    }

    /**
     * Test claim not found exception
     */
    @Test
    @DisplayName("Should throw ClaimNotFoundException when claim not found")
    void shouldThrowClaimNotFoundExceptionWhenClaimNotFound() {
        // Arrange
        Long claimId = 999L;
        when(claimRepository.findById(claimId)).thenReturn(Optional.empty());

        // Act & Assert
        ClaimNotFoundException exception = assertThrows(ClaimNotFoundException.class, () -> {
            claimService.getClaimById(claimId);
        });

        assertTrue(exception.getMessage().contains("Claim not found with ID: " + claimId));
        verify(claimRepository).findById(claimId);
        verify(claimMapper, never()).toResponseDto(any(Claim.class));
    }

    /**
     * Test successful claim update
     */
    @Test
    @DisplayName("Should update claim successfully")
    void shouldUpdateClaimSuccessfully() {
        // Arrange
        Long claimId = 1L;
        requestDto.setDescription("Updated description");
        requestDto.setStatus(ClaimStatus.UNDER_REVIEW); // Change status to avoid transition error
        
        when(claimRepository.findById(claimId)).thenReturn(Optional.of(claim));
        when(claimRepository.save(claim)).thenReturn(claim);
        when(claimMapper.toResponseDto(claim)).thenReturn(responseDto);

        // Act
        ClaimResponseDto result = claimService.updateClaim(claimId, requestDto);

        // Assert
        assertNotNull(result);
        assertEquals(responseDto.getId(), result.getId());

        // Verify interactions
        verify(claimRepository).findById(claimId);
        verify(claimMapper).updateEntityFromDto(requestDto, claim);
        verify(claimRepository).save(claim);
        verify(claimMapper).toResponseDto(claim);
    }

    /**
     * Test update of claim in terminal state
     */
    @Test
    @DisplayName("Should throw exception when updating claim in terminal state")
    void shouldThrowExceptionWhenUpdatingClaimInTerminalState() {
        // Arrange
        Long claimId = 1L;
        claim.setStatus(ClaimStatus.PAID); // Terminal state
        
        when(claimRepository.findById(claimId)).thenReturn(Optional.of(claim));

        // Act & Assert
        InvalidClaimStateException exception = assertThrows(InvalidClaimStateException.class, () -> {
            claimService.updateClaim(claimId, requestDto);
        });

        assertTrue(exception.getMessage().contains("terminal state"));
        verify(claimRepository).findById(claimId);
        verify(claimRepository, never()).save(any(Claim.class));
    }

    /**
     * Test successful status update
     */
    @Test
    @DisplayName("Should update claim status successfully")
    void shouldUpdateClaimStatusSuccessfully() {
        // Arrange
        Long claimId = 1L;
        ClaimStatus newStatus = ClaimStatus.UNDER_REVIEW;
        
        when(claimRepository.findById(claimId)).thenReturn(Optional.of(claim));
        when(claimRepository.save(claim)).thenReturn(claim);
        when(claimMapper.toResponseDto(claim)).thenReturn(responseDto);

        // Act
        ClaimResponseDto result = claimService.updateClaimStatus(claimId, newStatus);

        // Assert
        assertNotNull(result);
        assertEquals(ClaimStatus.UNDER_REVIEW, claim.getStatus());

        // Verify interactions
        verify(claimRepository).findById(claimId);
        verify(claimRepository).save(claim);
        verify(claimMapper).toResponseDto(claim);
    }

    /**
     * Test invalid status transition
     */
    @Test
    @DisplayName("Should throw exception for invalid status transition")
    void shouldThrowExceptionForInvalidStatusTransition() {
        // Arrange
        Long claimId = 1L;
        claim.setStatus(ClaimStatus.PAID); // Terminal state
        ClaimStatus newStatus = ClaimStatus.SUBMITTED; // Invalid transition
        
        when(claimRepository.findById(claimId)).thenReturn(Optional.of(claim));

        // Act & Assert
        InvalidClaimStateException exception = assertThrows(InvalidClaimStateException.class, () -> {
            claimService.updateClaimStatus(claimId, newStatus);
        });

        assertTrue(exception.getMessage().contains("Invalid status transition"));
        verify(claimRepository).findById(claimId);
        verify(claimRepository, never()).save(any(Claim.class));
    }

    /**
     * Test successful claim deletion
     */
    @Test
    @DisplayName("Should delete claim successfully")
    void shouldDeleteClaimSuccessfully() {
        // Arrange
        Long claimId = 1L;
        claim.setStatus(ClaimStatus.SUBMITTED); // Deletable state
        
        when(claimRepository.findById(claimId)).thenReturn(Optional.of(claim));

        // Act
        assertDoesNotThrow(() -> {
            claimService.deleteClaim(claimId);
        });

        // Verify interactions
        verify(claimRepository).findById(claimId);
        verify(claimRepository).delete(claim);
    }

    /**
     * Test deletion of non-deletable claim
     */
    @Test
    @DisplayName("Should throw exception when deleting non-deletable claim")
    void shouldThrowExceptionWhenDeletingNonDeletableClaim() {
        // Arrange
        Long claimId = 1L;
        claim.setStatus(ClaimStatus.APPROVED); // Non-deletable state
        
        when(claimRepository.findById(claimId)).thenReturn(Optional.of(claim));

        // Act & Assert
        InvalidClaimStateException exception = assertThrows(InvalidClaimStateException.class, () -> {
            claimService.deleteClaim(claimId);
        });

        assertTrue(exception.getMessage().contains("Cannot delete claim in status"));
        verify(claimRepository).findById(claimId);
        verify(claimRepository, never()).delete(any(Claim.class));
    }

    /**
     * Test pagination functionality
     */
    @Test
    @DisplayName("Should retrieve claims with pagination")
    void shouldRetrieveClaimsWithPagination() {
        // Arrange
        List<Claim> claims = Arrays.asList(claim);
        Page<Claim> claimsPage = new PageImpl<>(claims);
        Pageable pageable = PageRequest.of(0, 10);
        
        when(claimRepository.findAll(pageable)).thenReturn(claimsPage);
        when(claimMapper.toResponseDto(claim)).thenReturn(responseDto);

        // Act
        Page<ClaimResponseDto> result = claimService.getAllClaims(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());

        // Verify interactions
        verify(claimRepository).findAll(pageable);
    }

    /**
     * Test search functionality
     */
    @Test
    @DisplayName("Should search claims by criteria")
    void shouldSearchClaimsByCriteria() {
        // Arrange
        String policyNumber = "POL-2024-001";
        ClaimStatus status = ClaimStatus.SUBMITTED;
        String email = "john.doe@email.com";
        Pageable pageable = PageRequest.of(0, 10);
        
        List<Claim> claims = Arrays.asList(claim);
        Page<Claim> claimsPage = new PageImpl<>(claims);
        
        when(claimRepository.findByOptionalCriteria(policyNumber, status, email, pageable))
            .thenReturn(claimsPage);
        when(claimMapper.toResponseDto(claim)).thenReturn(responseDto);

        // Act
        Page<ClaimResponseDto> result = claimService.searchClaims(policyNumber, status, email, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        // Verify interactions
        verify(claimRepository).findByOptionalCriteria(policyNumber, status, email, pageable);
    }

    /**
     * Test claim number generation
     */
    @Test
    @DisplayName("Should generate unique claim number")
    void shouldGenerateUniqueClaimNumber() {
        // Arrange
        when(claimRepository.existsByClaimNumber(anyString())).thenReturn(false);

        // Act
        String claimNumber = claimService.generateClaimNumber();

        // Assert
        assertNotNull(claimNumber);
        assertTrue(claimNumber.startsWith("CLM-2026-")); // Updated to current year
        assertEquals(15, claimNumber.length()); // CLM-YYYY-NNNNNN format

        // Verify interaction
        verify(claimRepository).existsByClaimNumber(claimNumber);
    }

    /**
     * Test claim number generation with collision
     */
    @Test
    @DisplayName("Should handle claim number collision")
    void shouldHandleClaimNumberCollision() {
        // Arrange - First call returns true (collision), second returns false
        when(claimRepository.existsByClaimNumber(anyString()))
            .thenReturn(true)  // First attempt - collision
            .thenReturn(false); // Second attempt - success

        // Act
        String claimNumber = claimService.generateClaimNumber();

        // Assert
        assertNotNull(claimNumber);
        assertTrue(claimNumber.startsWith("CLM-2026-")); // Updated to current year

        // Verify that existsByClaimNumber was called twice due to collision
        verify(claimRepository, times(2)).existsByClaimNumber(anyString());
    }
}