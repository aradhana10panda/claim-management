package com.claimmanagement.repository;

import com.claimmanagement.model.entity.Claim;
import com.claimmanagement.model.entity.ClaimStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Claim entity data access operations
 * 
 * Spring Data JPA Repository Pattern:
 * - Extends JpaRepository to get CRUD operations automatically
 * - JpaRepository<Entity, ID> provides methods like save(), findById(), findAll(), delete()
 * - Spring Data JPA generates implementation at runtime using proxies
 * - Method names are parsed to generate queries automatically
 * 
 * Benefits of Repository Pattern:
 * 1. Abstraction: Hides data access complexity from business logic
 * 2. Testability: Easy to mock for unit testing
 * 3. Consistency: Standardized data access interface
 * 4. Flexibility: Can switch data sources without changing business logic
 * 
 * Query Methods:
 * - Spring Data JPA derives queries from method names
 * - Keywords: findBy, existsBy, countBy, deleteBy
 * - Operators: And, Or, Between, LessThan, GreaterThan, Like, In, etc.
 * - Custom queries using @Query annotation with JPQL or native SQL
 * 
 * @Repository annotation:
 * - Marks this as a Spring Data repository
 * - Enables exception translation (converts database exceptions to Spring exceptions)
 * - Makes the interface eligible for Spring's component scanning
 * 
 * @author Claim Management Team
 */
@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {

    /**
     * Find a claim by its unique claim number
     * 
     * Method naming convention: findBy + PropertyName
     * Spring Data JPA generates: SELECT * FROM claims WHERE claim_number = ?
     * 
     * @param claimNumber The unique claim number to search for
     * @return Optional containing the claim if found, empty otherwise
     */
    Optional<Claim> findByClaimNumber(String claimNumber);

    /**
     * Check if a claim exists with the given claim number
     * 
     * Method naming convention: existsBy + PropertyName
     * Spring Data JPA generates: SELECT COUNT(*) > 0 FROM claims WHERE claim_number = ?
     * More efficient than findBy when you only need to check existence
     * 
     * @param claimNumber The claim number to check
     * @return true if a claim with this number exists, false otherwise
     */
    boolean existsByClaimNumber(String claimNumber);

    /**
     * Find all claims for a specific policy number
     * 
     * Returns a list because one policy can have multiple claims
     * 
     * @param policyNumber The policy number to search for
     * @return List of claims associated with the policy
     */
    List<Claim> findByPolicyNumber(String policyNumber);

    /**
     * Find all claims with a specific status
     * 
     * Useful for administrative views and reporting
     * 
     * @param status The claim status to filter by
     * @return List of claims with the specified status
     */
    List<Claim> findByStatus(ClaimStatus status);

    /**
     * Find claims by claimant email address
     * 
     * Allows customers to view all their claims
     * 
     * @param claimantEmail The email address of the claimant
     * @return List of claims for the specified email
     */
    List<Claim> findByClaimantEmail(String claimantEmail);

    /**
     * Find claims created within a date range
     * 
     * Method naming: findBy + PropertyName + Between
     * Useful for reporting and analytics
     * 
     * @param startDate Start of the date range (inclusive)
     * @param endDate End of the date range (inclusive)
     * @return List of claims created within the specified date range
     */
    List<Claim> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find claims with amount greater than specified value
     * 
     * Method naming: findBy + PropertyName + GreaterThan
     * Useful for identifying high-value claims
     * 
     * @param amount The minimum claim amount
     * @return List of claims with amount greater than the specified value
     */
    List<Claim> findByClaimAmountGreaterThan(BigDecimal amount);

    /**
     * Find claims by status with pagination support
     * 
     * Pageable parameter enables pagination and sorting
     * Returns Page<T> which contains:
     * - List of entities for current page
     * - Total number of elements
     * - Total number of pages
     * - Current page information
     * 
     * @param status The claim status to filter by
     * @param pageable Pagination and sorting parameters
     * @return Page of claims with the specified status
     */
    Page<Claim> findByStatus(ClaimStatus status, Pageable pageable);

    /**
     * Find claims by multiple criteria with pagination
     * 
     * Method naming: findBy + Property1 + And + Property2
     * Combines multiple conditions with AND operator
     * 
     * @param policyNumber The policy number to filter by
     * @param status The claim status to filter by
     * @param pageable Pagination and sorting parameters
     * @return Page of claims matching both criteria
     */
    Page<Claim> findByPolicyNumberAndStatus(String policyNumber, ClaimStatus status, Pageable pageable);

    /**
     * Custom JPQL query to find claims by claimant name (case-insensitive partial match)
     * 
     * @Query annotation allows custom JPQL (Java Persistence Query Language) queries
     * JPQL is object-oriented query language (uses entity names, not table names)
     * 
     * Benefits of custom queries:
     * - Complex logic that can't be expressed in method names
     * - Performance optimization
     * - Advanced SQL features
     * 
     * @param claimantName The name to search for (partial match)
     * @return List of claims where claimant name contains the search term
     */
    @Query("SELECT c FROM Claim c WHERE LOWER(c.claimantName) LIKE LOWER(CONCAT('%', :claimantName, '%'))")
    List<Claim> findByClaimantNameContainingIgnoreCase(@Param("claimantName") String claimantName);

    /**
     * Custom JPQL query to find claims by multiple optional criteria
     * 
     * This query demonstrates:
     * - Optional parameters using OR conditions
     * - Parameter binding with @Param
     * - Complex WHERE clauses
     * 
     * @param policyNumber Optional policy number filter
     * @param status Optional status filter
     * @param claimantEmail Optional claimant email filter
     * @param pageable Pagination parameters
     * @return Page of claims matching the criteria
     */
    @Query("SELECT c FROM Claim c WHERE " +
           "(:policyNumber IS NULL OR c.policyNumber = :policyNumber) AND " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:claimantEmail IS NULL OR c.claimantEmail = :claimantEmail)")
    Page<Claim> findByOptionalCriteria(@Param("policyNumber") String policyNumber,
                                      @Param("status") ClaimStatus status,
                                      @Param("claimantEmail") String claimantEmail,
                                      Pageable pageable);

    /**
     * Custom JPQL query for claims summary statistics
     * 
     * Demonstrates aggregate functions in JPQL:
     * - COUNT(): Number of records
     * - SUM(): Total of numeric values
     * - AVG(): Average of numeric values
     * - MIN()/MAX(): Minimum/Maximum values
     * 
     * @param status The status to calculate statistics for
     * @return Array containing [count, total_amount, average_amount]
     */
    @Query("SELECT COUNT(c), SUM(c.claimAmount), AVG(c.claimAmount) FROM Claim c WHERE c.status = :status")
    Object[] getClaimStatisticsByStatus(@Param("status") ClaimStatus status);

    /**
     * Native SQL query example for complex database-specific operations
     * 
     * @Query with nativeQuery = true allows raw SQL
     * Use when JPQL limitations require database-specific features
     * 
     * Note: Native queries are less portable across different databases
     * 
     * @param days Number of days to look back
     * @return List of claim numbers created in the last N days
     */
    @Query(value = "SELECT claim_number FROM claims WHERE created_at >= DATEADD('DAY', -:days, CURRENT_TIMESTAMP)", 
           nativeQuery = true)
    List<String> findClaimNumbersCreatedInLastDays(@Param("days") int days);

    /**
     * Count claims by status
     * 
     * Method naming: countBy + PropertyName
     * Returns long instead of List<Entity>
     * 
     * @param status The status to count
     * @return Number of claims with the specified status
     */
    long countByStatus(ClaimStatus status);

    /**
     * Delete claims by policy number
     * 
     * Method naming: deleteBy + PropertyName
     * Spring Data JPA generates DELETE statement
     * 
     * Note: This performs individual DELETE operations for each entity
     * For bulk operations, consider using @Modifying @Query
     * 
     * @param policyNumber The policy number whose claims should be deleted
     * @return Number of deleted claims
     */
    long deleteByPolicyNumber(String policyNumber);
}