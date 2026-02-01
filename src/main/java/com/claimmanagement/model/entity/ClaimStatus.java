package com.claimmanagement.model.entity;

/**
 * Enumeration representing the various states of a claim in its lifecycle
 * 
 * Enums in Java are:
 * - Type-safe constants that prevent invalid values
 * - Implicitly final and extend java.lang.Enum
 * - Can have fields, methods, and constructors
 * - Provide compile-time safety for status values
 * 
 * Benefits of using enums for status:
 * 1. Type Safety: Prevents invalid status values
 * 2. Code Readability: Clear, self-documenting status names
 * 3. IDE Support: Auto-completion and refactoring support
 * 4. Performance: Efficient comparison using == operator
 * 
 * @author Claim Management Team
 */
public enum ClaimStatus {
    
    /**
     * SUBMITTED - Initial state when a claim is first submitted
     * This is typically the starting point of the claim lifecycle
     */
    SUBMITTED("Claim has been submitted and is awaiting initial review"),
    
    /**
     * UNDER_REVIEW - Claim is being actively reviewed by claims adjusters
     * Documents are being verified and investigation is in progress
     */
    UNDER_REVIEW("Claim is under review by claims department"),
    
    /**
     * APPROVED - Claim has been approved for payment
     * All requirements met and payment processing can begin
     */
    APPROVED("Claim has been approved for payment"),
    
    /**
     * REJECTED - Claim has been rejected
     * Does not meet policy requirements or insufficient documentation
     */
    REJECTED("Claim has been rejected"),
    
    /**
     * PAID - Claim has been paid out to the claimant
     * Final successful state of the claim process
     */
    PAID("Claim has been paid"),
    
    /**
     * CANCELLED - Claim has been cancelled
     * May be cancelled by claimant or due to policy issues
     */
    CANCELLED("Claim has been cancelled");

    /**
     * Human-readable description of the status
     * Useful for UI display and logging
     */
    private final String description;

    /**
     * Constructor for enum constants
     * Each enum constant calls this constructor with its description
     * 
     * @param description Human-readable description of the status
     */
    ClaimStatus(String description) {
        this.description = description;
    }

    /**
     * Gets the human-readable description of the status
     * 
     * @return Description of the claim status
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks if the current status allows transition to a new status
     * Implements business rules for valid status transitions
     * 
     * Business Rules:
     * - SUBMITTED can go to UNDER_REVIEW or CANCELLED
     * - UNDER_REVIEW can go to APPROVED, REJECTED, or CANCELLED
     * - APPROVED can go to PAID or CANCELLED
     * - REJECTED and PAID are terminal states (no further transitions)
     * - CANCELLED is a terminal state
     * 
     * @param newStatus The status to transition to
     * @return true if transition is allowed, false otherwise
     */
    public boolean canTransitionTo(ClaimStatus newStatus) {
        return switch (this) {
            case SUBMITTED -> newStatus == UNDER_REVIEW || newStatus == CANCELLED;
            case UNDER_REVIEW -> newStatus == APPROVED || newStatus == REJECTED || newStatus == CANCELLED;
            case APPROVED -> newStatus == PAID || newStatus == CANCELLED;
            case REJECTED, PAID, CANCELLED -> false; // Terminal states
        };
    }

    /**
     * Gets all valid next statuses from the current status
     * Useful for UI dropdowns and validation
     * 
     * @return Array of valid next statuses
     */
    public ClaimStatus[] getValidNextStatuses() {
        return switch (this) {
            case SUBMITTED -> new ClaimStatus[]{UNDER_REVIEW, CANCELLED};
            case UNDER_REVIEW -> new ClaimStatus[]{APPROVED, REJECTED, CANCELLED};
            case APPROVED -> new ClaimStatus[]{PAID, CANCELLED};
            case REJECTED, PAID, CANCELLED -> new ClaimStatus[]{}; // No valid transitions
        };
    }

    /**
     * Checks if the current status is a terminal state
     * Terminal states don't allow further transitions
     * 
     * @return true if this is a terminal status
     */
    public boolean isTerminal() {
        return this == REJECTED || this == PAID || this == CANCELLED;
    }

    /**
     * Checks if the current status represents a successful completion
     * 
     * @return true if the claim was successfully processed
     */
    public boolean isSuccessful() {
        return this == PAID;
    }

    /**
     * Override toString to return the enum name
     * This is useful for JSON serialization and logging
     * 
     * @return String representation of the enum
     */
    @Override
    public String toString() {
        return name();
    }
}