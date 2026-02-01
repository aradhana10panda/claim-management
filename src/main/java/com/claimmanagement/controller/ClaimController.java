package com.claimmanagement.controller;

import com.claimmanagement.model.dto.ClaimRequestDto;
import com.claimmanagement.model.dto.ClaimResponseDto;
import com.claimmanagement.model.entity.ClaimStatus;
import com.claimmanagement.service.ClaimService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller for Claim Management Operations
 * 
 * REST Controller Best Practices:
 * 1. Use appropriate HTTP methods (GET, POST, PUT, PATCH, DELETE)
 * 2. Follow RESTful URL conventions
 * 3. Use proper HTTP status codes
 * 4. Validate input using @Valid annotation
 * 5. Document APIs using OpenAPI/Swagger annotations
 * 6. Handle exceptions gracefully (delegated to GlobalExceptionHandler)
 * 7. Use DTOs for request/response to decouple API from internal models
 * 
 * Spring Annotations:
 * @RestController - Combines @Controller and @ResponseBody
 * @RequestMapping - Maps HTTP requests to handler methods
 * @GetMapping, @PostMapping, etc. - HTTP method-specific mappings
 * @PathVariable - Extracts values from URL path
 * @RequestParam - Extracts query parameters
 * @RequestBody - Binds HTTP request body to method parameter
 * @Valid - Triggers validation of request DTOs
 * 
 * OpenAPI/Swagger Annotations:
 * @Tag - Groups related operations
 * @Operation - Describes individual API operations
 * @ApiResponses - Documents possible response codes
 * @Parameter - Documents method parameters
 * @Schema - Provides schema information for request/response bodies
 * 
 * @author Claim Management Team
 */
@RestController
@RequestMapping("/claims")
@Tag(name = "Claim Management", description = "APIs for managing insurance claims")
public class ClaimController {

    /**
     * Logger for this controller
     */
    private static final Logger logger = LoggerFactory.getLogger(ClaimController.class);

    /**
     * Service layer dependency
     * Constructor injection is preferred over field injection
     */
    private final ClaimService claimService;

    /**
     * Constructor for dependency injection
     * 
     * @param claimService The claim service implementation
     */
    @Autowired
    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    /**
     * Creates a new claim
     * 
     * HTTP Method: POST
     * URL: /api/v1/claims
     * Request Body: ClaimRequestDto (JSON)
     * Response: ClaimResponseDto with HTTP 201 (Created)
     * 
     * @Valid annotation triggers validation of the request DTO
     * If validation fails, MethodArgumentNotValidException is thrown
     * and handled by GlobalExceptionHandler
     */
    @PostMapping
    @Operation(summary = "Create a new claim", 
               description = "Creates a new insurance claim with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Claim created successfully",
                    content = @Content(schema = @Schema(implementation = ClaimResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ClaimResponseDto> createClaim(
            @Valid @RequestBody ClaimRequestDto requestDto) {
        
        logger.info("Creating new claim for policy: {}", requestDto.getPolicyNumber());
        
        ClaimResponseDto responseDto = claimService.createClaim(requestDto);
        
        logger.info("Successfully created claim: {}", responseDto.getClaimNumber());
        
        // Return 201 Created with the created claim
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * Retrieves a claim by its ID
     * 
     * HTTP Method: GET
     * URL: /api/v1/claims/{id}
     * Path Variable: id (Long)
     * Response: ClaimResponseDto with HTTP 200 (OK)
     * 
     * @PathVariable extracts the {id} from the URL path
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get claim by ID", 
               description = "Retrieves a claim by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Claim found",
                    content = @Content(schema = @Schema(implementation = ClaimResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Claim not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ClaimResponseDto> getClaimById(
            @Parameter(description = "Unique identifier of the claim", required = true)
            @PathVariable Long id) {
        
        logger.debug("Retrieving claim by ID: {}", id);
        
        ClaimResponseDto responseDto = claimService.getClaimById(id);
        
        return ResponseEntity.ok(responseDto);
    }

    /**
     * Retrieves a claim by its claim number
     * 
     * HTTP Method: GET
     * URL: /api/v1/claims/number/{claimNumber}
     * Path Variable: claimNumber (String)
     * Response: ClaimResponseDto with HTTP 200 (OK)
     */
    @GetMapping("/number/{claimNumber}")
    @Operation(summary = "Get claim by claim number", 
               description = "Retrieves a claim by its business identifier (claim number)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Claim found"),
        @ApiResponse(responseCode = "404", description = "Claim not found")
    })
    public ResponseEntity<ClaimResponseDto> getClaimByNumber(
            @Parameter(description = "Unique claim number", required = true)
            @PathVariable String claimNumber) {
        
        logger.debug("Retrieving claim by number: {}", claimNumber);
        
        ClaimResponseDto responseDto = claimService.getClaimByNumber(claimNumber);
        
        return ResponseEntity.ok(responseDto);
    }

    /**
     * Retrieves all claims with pagination
     * 
     * HTTP Method: GET
     * URL: /api/v1/claims?page=0&size=10&sort=createdAt,desc
     * Query Parameters: page, size, sort
     * Response: Page<ClaimResponseDto> with HTTP 200 (OK)
     * 
     * Pagination Parameters:
     * - page: Page number (0-based)
     * - size: Number of items per page
     * - sort: Sort criteria (property,direction)
     * 
     * @RequestParam with defaultValue provides sensible defaults
     */
    @GetMapping
    @Operation(summary = "Get all claims with pagination", 
               description = "Retrieves all claims with pagination and sorting support")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Claims retrieved successfully")
    })
    public ResponseEntity<Page<ClaimResponseDto>> getAllClaims(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Sort criteria (property,direction)")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            
            @Parameter(description = "Sort direction (asc or desc)")
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        logger.debug("Retrieving all claims - page: {}, size: {}, sort: {} {}", 
                    page, size, sortBy, sortDirection);
        
        // Create Sort object
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? 
                                  Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        
        // Create Pageable object
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ClaimResponseDto> claimsPage = claimService.getAllClaims(pageable);
        
        return ResponseEntity.ok(claimsPage);
    }

    /**
     * Updates an existing claim
     * 
     * HTTP Method: PUT
     * URL: /api/v1/claims/{id}
     * Path Variable: id (Long)
     * Request Body: ClaimRequestDto (JSON)
     * Response: ClaimResponseDto with HTTP 200 (OK)
     * 
     * PUT is used for complete resource replacement
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a claim", 
               description = "Updates an existing claim with new information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Claim updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Claim not found"),
        @ApiResponse(responseCode = "409", description = "Claim cannot be modified in current state")
    })
    public ResponseEntity<ClaimResponseDto> updateClaim(
            @Parameter(description = "Unique identifier of the claim", required = true)
            @PathVariable Long id,
            
            @Valid @RequestBody ClaimRequestDto requestDto) {
        
        logger.info("Updating claim with ID: {}", id);
        
        ClaimResponseDto responseDto = claimService.updateClaim(id, requestDto);
        
        logger.info("Successfully updated claim: {}", responseDto.getClaimNumber());
        
        return ResponseEntity.ok(responseDto);
    }

    /**
     * Updates only the status of a claim
     * 
     * HTTP Method: PATCH
     * URL: /api/v1/claims/{id}/status
     * Path Variable: id (Long)
     * Request Parameter: status (ClaimStatus)
     * Response: ClaimResponseDto with HTTP 200 (OK)
     * 
     * PATCH is used for partial resource updates
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update claim status", 
               description = "Updates only the status of an existing claim")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Claim status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status transition"),
        @ApiResponse(responseCode = "404", description = "Claim not found")
    })
    public ResponseEntity<ClaimResponseDto> updateClaimStatus(
            @Parameter(description = "Unique identifier of the claim", required = true)
            @PathVariable Long id,
            
            @Parameter(description = "New status for the claim", required = true)
            @RequestParam ClaimStatus status) {
        
        logger.info("Updating claim status for ID: {} to {}", id, status);
        
        ClaimResponseDto responseDto = claimService.updateClaimStatus(id, status);
        
        logger.info("Successfully updated claim status: {} -> {}", 
                   responseDto.getClaimNumber(), status);
        
        return ResponseEntity.ok(responseDto);
    }

    /**
     * Deletes a claim
     * 
     * HTTP Method: DELETE
     * URL: /api/v1/claims/{id}
     * Path Variable: id (Long)
     * Response: HTTP 204 (No Content)
     * 
     * DELETE returns 204 No Content on successful deletion
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a claim", 
               description = "Deletes an existing claim (only allowed for SUBMITTED claims)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Claim deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Claim not found"),
        @ApiResponse(responseCode = "409", description = "Claim cannot be deleted in current state")
    })
    public ResponseEntity<Void> deleteClaim(
            @Parameter(description = "Unique identifier of the claim", required = true)
            @PathVariable Long id) {
        
        logger.info("Deleting claim with ID: {}", id);
        
        claimService.deleteClaim(id);
        
        logger.info("Successfully deleted claim with ID: {}", id);
        
        // Return 204 No Content
        return ResponseEntity.noContent().build();
    }

    /**
     * Searches claims by multiple criteria
     * 
     * HTTP Method: GET
     * URL: /api/v1/claims/search?policyNumber=POL123&status=SUBMITTED
     * Query Parameters: policyNumber, status, claimantEmail, page, size, sort
     * Response: Page<ClaimResponseDto> with HTTP 200 (OK)
     */
    @GetMapping("/search")
    @Operation(summary = "Search claims", 
               description = "Searches claims by multiple optional criteria")
    public ResponseEntity<Page<ClaimResponseDto>> searchClaims(
            @Parameter(description = "Policy number to filter by")
            @RequestParam(required = false) String policyNumber,
            
            @Parameter(description = "Claim status to filter by")
            @RequestParam(required = false) ClaimStatus status,
            
            @Parameter(description = "Claimant email to filter by")
            @RequestParam(required = false) String claimantEmail,
            
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        logger.debug("Searching claims with criteria - policy: {}, status: {}, email: {}", 
                    policyNumber, status, claimantEmail);
        
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? 
                                  Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ClaimResponseDto> claimsPage = claimService.searchClaims(
            policyNumber, status, claimantEmail, pageable);
        
        return ResponseEntity.ok(claimsPage);
    }

    /**
     * Gets claims by policy number
     * 
     * HTTP Method: GET
     * URL: /api/v1/claims/policy/{policyNumber}
     * Path Variable: policyNumber (String)
     * Response: List<ClaimResponseDto> with HTTP 200 (OK)
     */
    @GetMapping("/policy/{policyNumber}")
    @Operation(summary = "Get claims by policy number", 
               description = "Retrieves all claims associated with a specific policy")
    public ResponseEntity<List<ClaimResponseDto>> getClaimsByPolicyNumber(
            @Parameter(description = "Policy number", required = true)
            @PathVariable String policyNumber) {
        
        logger.debug("Retrieving claims for policy: {}", policyNumber);
        
        List<ClaimResponseDto> claims = claimService.getClaimsByPolicyNumber(policyNumber);
        
        return ResponseEntity.ok(claims);
    }

    /**
     * Gets claims by claimant email
     * 
     * HTTP Method: GET
     * URL: /api/v1/claims/claimant/{email}
     * Path Variable: email (String)
     * Response: List<ClaimResponseDto> with HTTP 200 (OK)
     */
    @GetMapping("/claimant/{email}")
    @Operation(summary = "Get claims by claimant email", 
               description = "Retrieves all claims for a specific claimant")
    public ResponseEntity<List<ClaimResponseDto>> getClaimsByClaimantEmail(
            @Parameter(description = "Claimant email address", required = true)
            @PathVariable String email) {
        
        logger.debug("Retrieving claims for claimant: {}", email);
        
        List<ClaimResponseDto> claims = claimService.getClaimsByClaimantEmail(email);
        
        return ResponseEntity.ok(claims);
    }

    /**
     * Gets high-value claims
     * 
     * HTTP Method: GET
     * URL: /api/v1/claims/high-value?minimumAmount=10000
     * Query Parameter: minimumAmount (BigDecimal)
     * Response: List<ClaimResponseDto> with HTTP 200 (OK)
     */
    @GetMapping("/high-value")
    @Operation(summary = "Get high-value claims", 
               description = "Retrieves claims above a specified amount threshold")
    public ResponseEntity<List<ClaimResponseDto>> getHighValueClaims(
            @Parameter(description = "Minimum claim amount threshold")
            @RequestParam(defaultValue = "10000") BigDecimal minimumAmount) {
        
        logger.debug("Retrieving high-value claims above: {}", minimumAmount);
        
        List<ClaimResponseDto> claims = claimService.getHighValueClaims(minimumAmount);
        
        return ResponseEntity.ok(claims);
    }

    /**
     * Gets claims created within a date range
     * 
     * HTTP Method: GET
     * URL: /api/v1/claims/date-range?startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59
     * Query Parameters: startDate, endDate (LocalDateTime)
     * Response: List<ClaimResponseDto> with HTTP 200 (OK)
     * 
     * @DateTimeFormat specifies the expected date format
     */
    @GetMapping("/date-range")
    @Operation(summary = "Get claims by date range", 
               description = "Retrieves claims created within a specified date range")
    public ResponseEntity<List<ClaimResponseDto>> getClaimsCreatedBetween(
            @Parameter(description = "Start date (inclusive)")
            @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) 
            LocalDateTime startDate,
            
            @Parameter(description = "End date (inclusive)")
            @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) 
            LocalDateTime endDate) {
        
        logger.debug("Retrieving claims created between {} and {}", startDate, endDate);
        
        List<ClaimResponseDto> claims = claimService.getClaimsCreatedBetween(startDate, endDate);
        
        return ResponseEntity.ok(claims);
    }

    /**
     * Searches claims by claimant name
     * 
     * HTTP Method: GET
     * URL: /api/v1/claims/search-by-name?name=John
     * Query Parameter: name (String)
     * Response: List<ClaimResponseDto> with HTTP 200 (OK)
     */
    @GetMapping("/search-by-name")
    @Operation(summary = "Search claims by claimant name", 
               description = "Searches claims by partial claimant name match")
    public ResponseEntity<List<ClaimResponseDto>> searchClaimsByClaimantName(
            @Parameter(description = "Claimant name to search for", required = true)
            @RequestParam String name) {
        
        logger.debug("Searching claims by claimant name: {}", name);
        
        List<ClaimResponseDto> claims = claimService.searchClaimsByClaimantName(name);
        
        return ResponseEntity.ok(claims);
    }

    /**
     * Gets claim count by status
     * 
     * HTTP Method: GET
     * URL: /api/v1/claims/count?status=SUBMITTED
     * Query Parameter: status (ClaimStatus)
     * Response: Long (count) with HTTP 200 (OK)
     */
    @GetMapping("/count")
    @Operation(summary = "Get claim count by status", 
               description = "Returns the number of claims with a specific status")
    public ResponseEntity<Long> getClaimCountByStatus(
            @Parameter(description = "Claim status to count", required = true)
            @RequestParam ClaimStatus status) {
        
        logger.debug("Getting claim count for status: {}", status);
        
        long count = claimService.getClaimCountByStatus(status);
        
        return ResponseEntity.ok(count);
    }
}