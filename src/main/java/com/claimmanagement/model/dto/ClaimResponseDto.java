package com.claimmanagement.model.dto;

import com.claimmanagement.model.entity.ClaimStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Claim Response
 * <p>
 * This DTO is used for:
 * 1. Returning claim data in API responses
 * 2. Controlling what information is exposed to clients
 * 3. Providing a stable API contract independent of entity changes
 * 4. Including computed or derived fields not stored in the entity
 * <p>
 * Key differences from ClaimRequestDto:
 * - Includes system-generated fields (id, claimNumber, timestamps)
 * - No validation annotations (data is already validated)
 * - Read-only representation of claim data
 * - May include additional computed fields
 *
 * @author Claim Management Team
 */
@Schema(description = "Response object containing claim information")
public class ClaimResponseDto {

    /**
     * Unique identifier of the claim (system-generated)
     */
    @Schema(description = "Unique identifier of the claim", example = "1")
    private Long id;

    /**
     * Business identifier for the claim (system-generated)
     */
    @Schema(description = "Unique claim number", example = "CLM-2024-001234")
    private String claimNumber;

    /**
     * Policy number associated with the claim
     */
    @Schema(description = "Policy number associated with the claim", example = "POL-2024-001234")
    private String policyNumber;

    /**
     * Name of the claimant
     */
    @Schema(description = "Full name of the claimant", example = "John Doe")
    private String claimantName;

    /**
     * Email address of the claimant
     */
    @Schema(description = "Email address of the claimant", example = "john.doe@email.com")
    private String claimantEmail;

    /**
     * Phone number of the claimant
     */
    @Schema(description = "Phone number of the claimant", example = "+1-555-123-4567")
    private String claimantPhone;

    /**
     * Description of the claim
     */
    @Schema(description = "Detailed description of the claim incident")
    private String description;

    /**
     * Amount being claimed
     */
    @Schema(description = "Amount being claimed in USD", example = "5000.00")
    private BigDecimal claimAmount;

    /**
     * Current status of the claim
     */
    @Schema(description = "Current status of the claim", example = "SUBMITTED")
    private ClaimStatus status;

    /**
     * Date and time when the incident occurred
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Date and time when the incident occurred", example = "2024-01-15T14:30:00")
    private LocalDateTime incidentDate;

    /**
     * Timestamp when the claim was created
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Date and time when the claim was created", example = "2024-01-16T09:00:00")
    private LocalDateTime createdAt;

    /**
     * Timestamp when the claim was last updated
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Date and time when the claim was last updated", example = "2024-01-16T10:30:00")
    private LocalDateTime updatedAt;

    /**
     * Default constructor for JSON serialization
     */
    public ClaimResponseDto() {
    }

    /**
     * Constructor with all fields
     * Useful for programmatic creation and testing
     */
    public ClaimResponseDto(Long id, String claimNumber, String policyNumber, String claimantName,
                            String claimantEmail, String claimantPhone, String description,
                            BigDecimal claimAmount, ClaimStatus status, LocalDateTime incidentDate,
                            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.claimNumber = claimNumber;
        this.policyNumber = policyNumber;
        this.claimantName = claimantName;
        this.claimantEmail = claimantEmail;
        this.claimantPhone = claimantPhone;
        this.description = description;
        this.claimAmount = claimAmount;
        this.status = status;
        this.incidentDate = incidentDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClaimNumber() {
        return claimNumber;
    }

    public void setClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Convenience method to check if claim is in a terminal state
     * This is a computed property not stored in the database
     *
     * @return true if the claim cannot be modified further
     */
    @Schema(description = "Indicates if the claim is in a terminal state")
    public boolean isTerminal() {
        return status != null && status.isTerminal();
    }

    /**
     * Convenience method to check if claim was successfully processed
     *
     * @return true if the claim was paid
     */
    @Schema(description = "Indicates if the claim was successfully processed")
    public boolean isSuccessful() {
        return status != null && status.isSuccessful();
    }

    /**
     * String representation for debugging and logging
     */
    @Override
    public String toString() {
        return "ClaimResponseDto{" +
                "id=" + id +
                ", claimNumber='" + claimNumber + '\'' +
                ", policyNumber='" + policyNumber + '\'' +
                ", claimantName='" + claimantName + '\'' +
                ", claimAmount=" + claimAmount +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}