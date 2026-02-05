package com.claimmanagement.model.dto;

import com.claimmanagement.model.entity.ClaimStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Claim Creation and Update Requests
 * <p>
 * DTOs (Data Transfer Objects) are used to:
 * 1. Transfer data between different layers of the application
 * 2. Provide a contract for API requests/responses
 * 3. Hide internal entity structure from external clients
 * 4. Allow different validation rules for different operations
 * 5. Prevent over-posting attacks (mass assignment)
 * <p>
 * Benefits of using DTOs:
 * - Decoupling: API structure independent of database structure
 * - Security: Control what data can be modified via API
 * - Validation: Different validation rules for different operations
 * - Versioning: API can evolve independently of entities
 * <p>
 * Jackson Annotations:
 *
 * @author Claim Management Team
 * @JsonFormat - Controls JSON serialization/deserialization format
 * <p>
 * Swagger/OpenAPI Annotations:
 * @Schema - Provides API documentation metadata
 */
@Schema(description = "Request object for creating or updating a claim")
public class ClaimRequestDto {

    /**
     * Policy number associated with the claim
     * Required field with length validation
     */
    @NotBlank(message = "Policy number is required")
    @Size(min = 5, max = 50, message = "Policy number must be between 5 and 50 characters")
    @Schema(description = "Policy number associated with the claim",
            example = "POL-2024-001234",
            required = true)
    private String policyNumber;

    /**
     * Name of the person making the claim
     */
    @NotBlank(message = "Claimant name is required")
    @Size(min = 2, max = 100, message = "Claimant name must be between 2 and 100 characters")
    @Schema(description = "Full name of the claimant",
            example = "John Doe",
            required = true)
    private String claimantName;

    /**
     * Email address of the claimant
     * Must be a valid email format
     */
    @NotBlank(message = "Claimant email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Schema(description = "Email address of the claimant",
            example = "john.doe@email.com",
            required = true)
    private String claimantEmail;

    /**
     * Phone number of the claimant (optional)
     * Pattern validation for common phone number formats
     */
    @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]{10,20}$",
            message = "Please provide a valid phone number")
    @Schema(description = "Phone number of the claimant",
            example = "+1-555-123-4567")
    private String claimantPhone;

    /**
     * Detailed description of the claim
     */
    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    @Schema(description = "Detailed description of the claim incident",
            example = "Vehicle collision at intersection of Main St and Oak Ave",
            required = true)
    private String description;

    /**
     * Monetary amount being claimed
     * Must be positive and within reasonable limits
     */
    @NotNull(message = "Claim amount is required")
    @DecimalMin(value = "0.01", message = "Claim amount must be greater than 0")
    @DecimalMax(value = "1000000.00", message = "Claim amount cannot exceed $1,000,000")
    @Digits(integer = 8, fraction = 2, message = "Claim amount format is invalid")
    @Schema(description = "Amount being claimed in USD",
            example = "5000.00",
            required = true)
    private BigDecimal claimAmount;

    /**
     * Status of the claim (optional for creation, defaults to SUBMITTED)
     * For updates, this allows status transitions
     */
    @Schema(description = "Current status of the claim",
            example = "SUBMITTED",
            allowableValues = {"SUBMITTED", "UNDER_REVIEW", "APPROVED", "REJECTED", "PAID", "CANCELLED"})
    private ClaimStatus status;

    /**
     * Date and time when the incident occurred
     * Must be in the past (cannot claim for future incidents)
     *
     * @JsonFormat ensures consistent date format in JSON
     * ISO 8601 format: yyyy-MM-dd'T'HH:mm:ss
     */
    @NotNull(message = "Incident date is required")
    @PastOrPresent(message = "Incident date cannot be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Date and time when the incident occurred",
            example = "2024-01-15T14:30:00",
            required = true)
    private LocalDateTime incidentDate;

    /**
     * Default constructor required for JSON deserialization
     * Jackson uses this to create instances when parsing JSON
     */
    public ClaimRequestDto() {
    }

    /**
     * Constructor for creating request DTOs with all fields
     * Useful for testing and programmatic creation
     */
    public ClaimRequestDto(String policyNumber, String claimantName, String claimantEmail,
                           String claimantPhone, String description, BigDecimal claimAmount,
                           ClaimStatus status, LocalDateTime incidentDate) {
        this.policyNumber = policyNumber;
        this.claimantName = claimantName;
        this.claimantEmail = claimantEmail;
        this.claimantPhone = claimantPhone;
        this.description = description;
        this.claimAmount = claimAmount;
        this.status = status;
        this.incidentDate = incidentDate;
    }

    // Getters and Setters

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getClaimantName() {
        return claimantName;
    }

    public void setClaimantName(String claimantName) {
        this.claimantName = claimantName;
    }

    public String getClaimantEmail() {
        return claimantEmail;
    }

    public void setClaimantEmail(String claimantEmail) {
        this.claimantEmail = claimantEmail;
    }

    public String getClaimantPhone() {
        return claimantPhone;
    }

    public void setClaimantPhone(String claimantPhone) {
        this.claimantPhone = claimantPhone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(BigDecimal claimAmount) {
        this.claimAmount = claimAmount;
    }

    public ClaimStatus getStatus() {
        return status;
    }

    public void setStatus(ClaimStatus status) {
        this.status = status;
    }

    public LocalDateTime getIncidentDate() {
        return incidentDate;
    }

    public void setIncidentDate(LocalDateTime incidentDate) {
        this.incidentDate = incidentDate;
    }

    /**
     * String representation for debugging and logging
     */
    @Override
    public String toString() {
        return "ClaimRequestDto{" +
                "policyNumber='" + policyNumber + '\'' +
                ", claimantName='" + claimantName + '\'' +
                ", claimantEmail='" + claimantEmail + '\'' +
                ", claimAmount=" + claimAmount +
                ", status=" + status +
                ", incidentDate=" + incidentDate +
                '}';
    }
}