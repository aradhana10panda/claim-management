package com.claimmanagement.service.impl;

import com.claimmanagement.exception.ClaimNotFoundException;
import com.claimmanagement.exception.InvalidClaimStateException;
import com.claimmanagement.mapper.ClaimMapper;
import com.claimmanagement.model.dto.ClaimRequestDto;
import com.claimmanagement.model.dto.ClaimResponseDto;
import com.claimmanagement.model.entity.Claim;
import com.claimmanagement.model.entity.ClaimStatus;
import com.claimmanagement.repository.ClaimRepository;
import com.claimmanagement.service.ClaimService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Implementation of ClaimService interface
 * 
 * Service Implementation Best Practices:
 * 1. Transaction Management: Use @Transactional for data consistency
 * 2. Exception Handling: Convert technical exceptions to business exceptions
 * 3. Logging: Log important business events and errors
 * 4. Validation: Implement business rule validation
 * 5. Performance: Optimize database queries and caching
 * 
 * Spring Annotations:
 * @Service - Marks this as a Spring service component
 * @Transactional - Manages database transactions automatically
 * @Autowired - Dependency injection (constructor injection preferred)
 * 
 * Transaction Management:
 * - @Transactional ensures ACID properties
 * - Rollback on RuntimeException by default
 * - Can specify isolation levels and propagation behavior
 * - readOnly = true optimizes read-only operations
 * 
 * @author Claim Management Team
 */
@Service
@Transactional  // All methods run in transactions by default
public class ClaimServiceImpl implements ClaimService {

    /**
     * Logger for this class
     * SLF4J (Simple Logging Facade for Java) provides:
     * - Abstraction over logging frameworks
     * - Parameterized logging for performance
     * - Level-based logging (TRACE, DEBUG, INFO, WARN, ERROR)
     */
    private static final Logger logger = LoggerFactory.getLogger(ClaimServiceImpl.class);

    /**
     * Repository for database operations
     * Final field ensures immutability after construction
     */
    private final ClaimRepository claimRepository;

    /**
     * Mapper for entity-DTO conversions
     * MapStruct generates the implementation
     */
    private final ClaimMapper claimMapper;

    /**
     * Constructor-based dependency injection (recommended over field injection)
     * 
     * Benefits of constructor injection:
     * 1. Immutable dependencies (final fields)
     * 2. Fail-fast if dependencies are missing
     * 3. Easy to test (no reflection needed)
     * 4. Clear dependencies in constructor signature
     * 
     * @param claimRepository Repository for claim data access
     * @param claimMapper Mapper for entity-DTO conversions
     */
    @Autowired
    public ClaimServiceImpl(ClaimRepository claimRepository, ClaimMapper claimMapper) {
        this.claimRepository = claimRepository;
        this.claimMapper = claimMapper;
    }

    /**
     * Creates a new claim with business logic validation
     * 
     * Transaction behavior: Creates new transaction if none exists
     * Rollback: Automatic on any RuntimeException
     */
    @Override
    @Transactional
    public ClaimResponseDto createClaim(ClaimRequestDto requestDto) {
        logger.info("Creating new claim for policy: {}", requestDto.getPolicyNumber());

        try {
            // Convert DTO to entity using MapStruct
            Claim claim = claimMapper.toEntity(requestDto);

            // Generate unique claim number
            String claimNumber = generateClaimNumber();
            claim.setClaimNumber(claimNumber);

            // Set default status if not provided
            if (claim.getStatus() == null) {
                claim.setStatus(ClaimStatus.SUBMITTED);
            }

            // Validate business rules
            validateClaimCreation(claim);

            // Save to database
            Claim savedClaim = claimRepository.save(claim);

            logger.info("Successfully created claim with number: {}", savedClaim.getClaimNumber());

            // Convert entity back to DTO for response
            return claimMapper.toResponseDto(savedClaim);

        } catch (Exception e) {
            logger.error("Error creating claim for policy: {}", requestDto.getPolicyNumber(), e);
            throw new RuntimeException("Failed to create claim: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves claim by ID with error handling
     * 
     * @Transactional(readOnly = true) optimizes for read operations:
     * - No dirty checking for entities
     * - Database can optimize for read-only queries
     * - Prevents accidental modifications
     */
    @Override
    @Transactional(readOnly = true)
    public ClaimResponseDto getClaimById(Long id) {
        logger.debug("Retrieving claim by ID: {}", id);

        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException("Claim not found with ID: " + id));

        return claimMapper.toResponseDto(claim);
    }

    @Override
    @Transactional(readOnly = true)
    public ClaimResponseDto getClaimByNumber(String claimNumber) {
        logger.debug("Retrieving claim by number: {}", claimNumber);

        Claim claim = claimRepository.findByClaimNumber(claimNumber)
                .orElseThrow(() -> new ClaimNotFoundException("Claim not found with number: " + claimNumber));

        return claimMapper.toResponseDto(claim);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClaimResponseDto> getAllClaims(Pageable pageable) {
        logger.debug("Retrieving all claims with pagination: page={}, size={}", 
                    pageable.getPageNumber(), pageable.getPageSize());

        Page<Claim> claimsPage = claimRepository.findAll(pageable);
        
        // Convert Page<Claim> to Page<ClaimResponseDto>
        return claimsPage.map(claimMapper::toResponseDto);
    }

    /**
     * Updates existing claim with business rule validation
     */
    @Override
    @Transactional
    public ClaimResponseDto updateClaim(Long id, ClaimRequestDto requestDto) {
        logger.info("Updating claim with ID: {}", id);

        // Retrieve existing claim
        Claim existingClaim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException("Claim not found with ID: " + id));

        // Check if claim can be modified
        if (existingClaim.getStatus().isTerminal()) {
            throw new InvalidClaimStateException(
                "Cannot modify claim in terminal state: " + existingClaim.getStatus());
        }

        // Validate status transition if status is being changed
        if (requestDto.getStatus() != null && 
            !existingClaim.getStatus().canTransitionTo(requestDto.getStatus())) {
            throw new InvalidClaimStateException(
                String.format("Invalid status transition from %s to %s", 
                            existingClaim.getStatus(), requestDto.getStatus()));
        }

        // Update entity with new data using MapStruct
        claimMapper.updateEntityFromDto(requestDto, existingClaim);

        // Validate updated claim
        validateClaimUpdate(existingClaim);

        // Save updated claim
        Claim updatedClaim = claimRepository.save(existingClaim);

        logger.info("Successfully updated claim: {}", updatedClaim.getClaimNumber());

        return claimMapper.toResponseDto(updatedClaim);
    }

    @Override
    @Transactional
    public ClaimResponseDto updateClaimStatus(Long id, ClaimStatus newStatus) {
        logger.info("Updating claim status for ID: {} to {}", id, newStatus);

        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException("Claim not found with ID: " + id));

        // Validate status transition
        if (!claim.getStatus().canTransitionTo(newStatus)) {
            throw new InvalidClaimStateException(
                String.format("Invalid status transition from %s to %s", 
                            claim.getStatus(), newStatus));
        }

        claim.setStatus(newStatus);
        Claim updatedClaim = claimRepository.save(claim);

        logger.info("Successfully updated claim status: {} -> {}", 
                   claim.getClaimNumber(), newStatus);

        return claimMapper.toResponseDto(updatedClaim);
    }

    @Override
    @Transactional
    public void deleteClaim(Long id) {
        logger.info("Deleting claim with ID: {}", id);

        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException("Claim not found with ID: " + id));

        // Business rule: Only allow deletion of claims in SUBMITTED status
        if (claim.getStatus() != ClaimStatus.SUBMITTED) {
            throw new InvalidClaimStateException(
                "Cannot delete claim in status: " + claim.getStatus());
        }

        claimRepository.delete(claim);
        logger.info("Successfully deleted claim: {}", claim.getClaimNumber());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClaimResponseDto> searchClaims(String policyNumber, ClaimStatus status, 
                                              String claimantEmail, Pageable pageable) {
        logger.debug("Searching claims with criteria - policy: {}, status: {}, email: {}", 
                    policyNumber, status, claimantEmail);

        Page<Claim> claimsPage = claimRepository.findByOptionalCriteria(
            policyNumber, status, claimantEmail, pageable);

        return claimsPage.map(claimMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimResponseDto> getClaimsByPolicyNumber(String policyNumber) {
        logger.debug("Retrieving claims for policy: {}", policyNumber);

        List<Claim> claims = claimRepository.findByPolicyNumber(policyNumber);
        return claimMapper.toResponseDtoList(claims);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimResponseDto> getClaimsByClaimantEmail(String claimantEmail) {
        logger.debug("Retrieving claims for claimant: {}", claimantEmail);

        List<Claim> claims = claimRepository.findByClaimantEmail(claimantEmail);
        return claimMapper.toResponseDtoList(claims);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClaimResponseDto> getClaimsByStatus(ClaimStatus status, Pageable pageable) {
        logger.debug("Retrieving claims with status: {}", status);

        Page<Claim> claimsPage = claimRepository.findByStatus(status, pageable);
        return claimsPage.map(claimMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimResponseDto> getHighValueClaims(BigDecimal minimumAmount) {
        logger.debug("Retrieving high-value claims above: {}", minimumAmount);

        List<Claim> claims = claimRepository.findByClaimAmountGreaterThan(minimumAmount);
        return claimMapper.toResponseDtoList(claims);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimResponseDto> getClaimsCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        logger.debug("Retrieving claims created between {} and {}", startDate, endDate);

        List<Claim> claims = claimRepository.findByCreatedAtBetween(startDate, endDate);
        return claimMapper.toResponseDtoList(claims);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimResponseDto> searchClaimsByClaimantName(String claimantName) {
        logger.debug("Searching claims by claimant name: {}", claimantName);

        List<Claim> claims = claimRepository.findByClaimantNameContainingIgnoreCase(claimantName);
        return claimMapper.toResponseDtoList(claims);
    }

    @Override
    @Transactional(readOnly = true)
    public long getClaimCountByStatus(ClaimStatus status) {
        return claimRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByClaimNumber(String claimNumber) {
        return claimRepository.existsByClaimNumber(claimNumber);
    }

    /**
     * Generates unique claim number with retry logic
     * 
     * Format: CLM-YYYY-NNNNNN
     * - CLM: Prefix for claim
     * - YYYY: Current year
     * - NNNNNN: 6-digit sequential number
     * 
     * Collision handling: Retry with different random number if collision occurs
     */
    @Override
    public String generateClaimNumber() {
        int currentYear = Year.now().getValue();
        int maxRetries = 10;
        
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            // Generate random 6-digit number
            int randomNumber = ThreadLocalRandom.current().nextInt(100000, 999999);
            String claimNumber = String.format("CLM-%d-%06d", currentYear, randomNumber);
            
            // Check if this number already exists
            if (!existsByClaimNumber(claimNumber)) {
                logger.debug("Generated unique claim number: {}", claimNumber);
                return claimNumber;
            }
            
            logger.debug("Claim number collision detected: {}, retrying...", claimNumber);
        }
        
        throw new RuntimeException("Failed to generate unique claim number after " + maxRetries + " attempts");
    }

    /**
     * Validates business rules for claim creation
     * 
     * @param claim The claim to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateClaimCreation(Claim claim) {
        // Business Rule: Claim amount must be positive
        if (claim.getClaimAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Claim amount must be greater than zero");
        }

        // Business Rule: Incident date cannot be in the future
        if (claim.getIncidentDate().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Incident date cannot be in the future");
        }

        // Business Rule: Incident date cannot be more than 2 years old
        LocalDateTime twoYearsAgo = LocalDateTime.now().minusYears(2);
        if (claim.getIncidentDate().isBefore(twoYearsAgo)) {
            throw new IllegalArgumentException("Incident date cannot be more than 2 years old");
        }

        logger.debug("Claim creation validation passed for: {}", claim.getClaimNumber());
    }

    /**
     * Validates business rules for claim updates
     * 
     * @param claim The claim to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateClaimUpdate(Claim claim) {
        // Reuse creation validation rules
        validateClaimCreation(claim);

        // Additional update-specific validations can be added here
        logger.debug("Claim update validation passed for: {}", claim.getClaimNumber());
    }
}