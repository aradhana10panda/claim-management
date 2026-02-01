package com.claimmanagement.service;

import com.claimmanagement.model.dto.ClaimRequestDto;
import com.claimmanagement.model.dto.ClaimResponseDto;
import com.claimmanagement.model.entity.ClaimStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service Interface for Claim Management Operations
 * 
 * Service Layer in Spring Architecture:
 * - Contains business logic and rules
 * - Orchestrates operations between controllers and repositories
 * - Handles transactions and data transformations
 * - Provides abstraction for complex operations
 * 
 * Benefits of Service Interface:
 * 1. Abstraction: Hides implementation details from controllers
 * 2. Testability: Easy to mock for unit testing
 * 3. Flexibility: Can have multiple implementations
 * 4. Documentation: Serves as a contract for business operations
 * 5. Dependency Inversion: Controllers depend on abstraction, not concrete implementation
 * 
 * Interface Design Principles:
 * - Methods represent business operations, not CRUD operations
 * - Parameters and return types use DTOs, not entities
 * - Method names are business-focused (createClaim vs save)
 * - Exceptions are business-meaningful
 * 
 * @author Claim Management Team
 */
public interface ClaimService {

    /**
     * Creates a new claim in the system
     * 
     * Business Logic:
     * 1. Validates the request data
     * 2. Generates unique claim number
     * 3. Sets initial status to SUBMITTED
     * 4. Saves the claim to database
     * 5. Returns the created claim with system-generated fields
     * 
     * @param requestDto The claim data from the client
     * @return The created claim with generated ID and claim number
     * @throws IllegalArgumentException if request data is invalid
     * @throws RuntimeException if claim number generation fails
     */
    ClaimResponseDto createClaim(ClaimRequestDto requestDto);

    /**
     * Retrieves a claim by its unique identifier
     * 
     * @param id The unique identifier of the claim
     * @return The claim information
     * @throws ClaimNotFoundException if no claim exists with the given ID
     */
    ClaimResponseDto getClaimById(Long id);

    /**
     * Retrieves a claim by its business identifier (claim number)
     * 
     * @param claimNumber The unique claim number
     * @return The claim information
     * @throws ClaimNotFoundException if no claim exists with the given claim number
     */
    ClaimResponseDto getClaimByNumber(String claimNumber);

    /**
     * Retrieves all claims with pagination support
     * 
     * Pagination is essential for:
     * - Performance: Avoid loading large datasets
     * - User Experience: Manageable data display
     * - Memory Management: Prevent OutOfMemoryError
     * 
     * @param pageable Pagination and sorting parameters
     * @return Page of claims with metadata (total count, page info)
     */
    Page<ClaimResponseDto> getAllClaims(Pageable pageable);

    /**
     * Updates an existing claim
     * 
     * Business Logic:
     * 1. Validates the claim exists
     * 2. Checks if claim can be modified (not in terminal state)
     * 3. Validates status transitions if status is being changed
     * 4. Updates the claim data
     * 5. Returns the updated claim
     * 
     * @param id The ID of the claim to update
     * @param requestDto The updated claim data
     * @return The updated claim information
     * @throws ClaimNotFoundException if claim doesn't exist
     * @throws IllegalStateException if claim cannot be modified
     * @throws IllegalArgumentException if status transition is invalid
     */
    ClaimResponseDto updateClaim(Long id, ClaimRequestDto requestDto);

    /**
     * Updates only the status of a claim
     * 
     * Specialized method for status changes with business rule validation
     * 
     * @param id The ID of the claim
     * @param newStatus The new status to set
     * @return The updated claim information
     * @throws ClaimNotFoundException if claim doesn't exist
     * @throws IllegalStateException if status transition is not allowed
     */
    ClaimResponseDto updateClaimStatus(Long id, ClaimStatus newStatus);

    /**
     * Deletes a claim from the system
     * 
     * Business Logic:
     * 1. Validates the claim exists
     * 2. Checks if claim can be deleted (business rules)
     * 3. Performs soft or hard delete based on business requirements
     * 
     * Note: In real systems, claims are rarely deleted due to audit requirements
     * Consider implementing soft delete or archival instead
     * 
     * @param id The ID of the claim to delete
     * @throws ClaimNotFoundException if claim doesn't exist
     * @throws IllegalStateException if claim cannot be deleted
     */
    void deleteClaim(Long id);

    /**
     * Searches claims by various criteria
     * 
     * Advanced search functionality with multiple optional filters
     * 
     * @param policyNumber Optional policy number filter
     * @param status Optional status filter
     * @param claimantEmail Optional claimant email filter
     * @param pageable Pagination and sorting parameters
     * @return Page of claims matching the search criteria
     */
    Page<ClaimResponseDto> searchClaims(String policyNumber, ClaimStatus status, 
                                       String claimantEmail, Pageable pageable);

    /**
     * Retrieves all claims for a specific policy
     * 
     * Business use case: Customer service representatives need to see all claims for a policy
     * 
     * @param policyNumber The policy number to search for
     * @return List of claims associated with the policy
     */
    List<ClaimResponseDto> getClaimsByPolicyNumber(String policyNumber);

    /**
     * Retrieves all claims for a specific claimant
     * 
     * Business use case: Customers want to see all their claims
     * 
     * @param claimantEmail The email address of the claimant
     * @return List of claims for the specified claimant
     */
    List<ClaimResponseDto> getClaimsByClaimantEmail(String claimantEmail);

    /**
     * Retrieves claims by status with pagination
     * 
     * Business use case: Claims adjusters need to see all claims in specific states
     * 
     * @param status The claim status to filter by
     * @param pageable Pagination and sorting parameters
     * @return Page of claims with the specified status
     */
    Page<ClaimResponseDto> getClaimsByStatus(ClaimStatus status, Pageable pageable);

    /**
     * Retrieves high-value claims above a specified amount
     * 
     * Business use case: Management oversight for high-value claims
     * 
     * @param minimumAmount The minimum claim amount threshold
     * @return List of claims above the specified amount
     */
    List<ClaimResponseDto> getHighValueClaims(BigDecimal minimumAmount);

    /**
     * Retrieves claims created within a date range
     * 
     * Business use case: Reporting and analytics
     * 
     * @param startDate Start of the date range (inclusive)
     * @param endDate End of the date range (inclusive)
     * @return List of claims created within the specified period
     */
    List<ClaimResponseDto> getClaimsCreatedBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Searches claims by claimant name (partial match)
     * 
     * Business use case: Customer service search functionality
     * 
     * @param claimantName Partial or full claimant name to search for
     * @return List of claims where claimant name contains the search term
     */
    List<ClaimResponseDto> searchClaimsByClaimantName(String claimantName);

    /**
     * Gets count of claims by status
     * 
     * Business use case: Dashboard statistics and reporting
     * 
     * @param status The status to count
     * @return Number of claims with the specified status
     */
    long getClaimCountByStatus(ClaimStatus status);

    /**
     * Checks if a claim number already exists
     * 
     * Business use case: Validation during claim creation
     * 
     * @param claimNumber The claim number to check
     * @return true if the claim number exists, false otherwise
     */
    boolean existsByClaimNumber(String claimNumber);

    /**
     * Generates a unique claim number
     * 
     * Business Logic:
     * - Format: CLM-YYYY-NNNNNN (e.g., CLM-2024-000001)
     * - Year-based numbering for easy identification
     * - Sequential numbering within each year
     * - Collision detection and retry logic
     * 
     * @return A unique claim number
     */
    String generateClaimNumber();
}