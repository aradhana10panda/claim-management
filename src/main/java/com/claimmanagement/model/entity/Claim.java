package com.claimmanagement.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Claim Entity - Represents a claim in the database
 * <p>
 * JPA Annotations Explained:
 *
 * @author Claim Management Team
 * @Entity - Marks this class as a JPA entity (database table)
 * @Table - Specifies the table name and constraints
 * @Id - Marks the primary key field
 * @GeneratedValue - Specifies how the primary key is generated
 * @Column - Maps field to database column with constraints
 * @Enumerated - Maps enum to database (STRING stores enum name, ORDINAL stores position)
 * <p>
 * Hibernate Annotations:
 * @CreationTimestamp - Automatically sets timestamp when entity is created
 * @UpdateTimestamp - Automatically updates timestamp when entity is modified
 * <p>
 * Validation Annotations:
 * @NotBlank - Field cannot be null or empty (for Strings)
 * @NotNull - Field cannot be null
 * @Size - Validates string length
 * @DecimalMin - Validates minimum decimal value
 * @Email - Validates email format
 */
@Entity
@Table(name = "claims",
        indexes = {
                @Index(name = "idx_claim_number", columnList = "claimNumber"),
                @Index(name = "idx_policy_number", columnList = "policyNumber"),
                @Index(name = "idx_status", columnList = "status")
        })
public class Claim {

    /**
     * Primary Key - Auto-generated using IDENTITY strategy
     * IDENTITY strategy uses database auto-increment feature
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Unique claim number - Business identifier for the claim
     *
     * @Column annotations specify database constraints
     */
    @NotBlank(message = "Claim number is required")
    @Size(max = 50, message = "Claim number must not exceed 50 characters")
    @Column(name = "claim_number", nullable = false, unique = true, length = 50)
    private String claimNumber;

    /**
     * Policy number associated with this claim
     */
    @NotBlank(message = "Policy number is required")
    @Size(max = 50, message = "Policy number must not exceed 50 characters")
    @Column(name = "policy_number", nullable = false, length = 50)
    private String policyNumber;

    /**
     * Name of the claimant (person making the claim)
     */
    @NotBlank(message = "Claimant name is required")
    @Size(max = 100, message = "Claimant name must not exceed 100 characters")
    @Column(name = "claimant_name", nullable = false, length = 100)
    private String claimantName;

    /**
     * Email address of the claimant
     */
    @NotBlank(message = "Claimant email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Column(name = "claimant_email", nullable = false, length = 100)
    private String claimantEmail;

    /**
     * Phone number of the claimant
     */
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    @Column(name = "claimant_phone", length = 20)
    private String claimantPhone;

    /**
     * Description of the claim
     */
    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    /**
     * Claim amount - Using BigDecimal for precise monetary calculations
     * precision = 10: total number of digits
     * scale = 2: number of digits after decimal point
     */
    @NotNull(message = "Claim amount is required")
    @DecimalMin(value = "0.01", message = "Claim amount must be greater than 0")
    @Column(name = "claim_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal claimAmount;

    /**
     * Current status of the claim
     *
     * @Enumerated(EnumType.STRING) stores the enum name as string in database
     * This is preferred over ORDINAL as it's more readable and maintainable
     */
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ClaimStatus status;

    /**
     * Date when the incident occurred
     */
    @NotNull(message = "Incident date is required")
    @Column(name = "incident_date", nullable = false)
    private LocalDateTime incidentDate;

    /**
     * Automatically set when the claim is created
     *
     * @CreationTimestamp annotation handles this automatically
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Automatically updated when the claim is modified
     *
     * @UpdateTimestamp annotation handles this automatically
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Default constructor required by JPA
     * JPA uses reflection to create instances
     */
    public Claim() {
    }

    /**
     * Constructor for creating new claims
     * Excludes id, createdAt, updatedAt as they are auto-generated
     */
    public Claim(String claimNumber, String policyNumber, String claimantName,
                 String claimantEmail, String claimantPhone, String description,
                 BigDecimal claimAmount, ClaimStatus status, LocalDateTime incidentDate) {
        this.claimNumber = claimNumber;
        this.policyNumber = policyNumber;
        this.claimantName = claimantName;
        this.claimantEmail = claimantEmail;
        this.claimantPhone = claimantPhone;
        this.description = description;
        this.claimAmount = claimAmount;
        this.status = status;
        this.incidentDate = incidentDate;
    }

    // Getters and Setters with JavaDoc comments

    /**
     * Gets the unique identifier of the claim
     *
     * @return the claim ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the claim
     * Note: This should typically only be used by JPA
     *
     * @param id the claim ID
     */
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
     * Equals method for entity comparison
     * Uses business key (claimNumber) instead of ID for comparison
     * This is important for entities that might not have IDs yet
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Claim claim = (Claim) o;
        return Objects.equals(claimNumber, claim.claimNumber);
    }

    /**
     * HashCode method consistent with equals
     * Uses business key (claimNumber) for hash calculation
     */
    @Override
    public int hashCode() {
        return Objects.hash(claimNumber);
    }

    /**
     * String representation of the claim
     * Useful for debugging and logging
     */
    @Override
    public String toString() {
        return "Claim{" +
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