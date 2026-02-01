package com.claimmanagement.exception;

import com.claimmanagement.model.entity.ClaimStatus;

/**
 * Custom exception for invalid claim state operations
 * 
 * This exception is thrown when:
 * 1. Attempting to modify a claim in a terminal state
 * 2. Invalid status transitions are attempted
 * 3. Business rules prevent certain operations based on claim state
 * 4. Workflow violations occur
 * 
 * State Management in Business Applications:
 * - Entities often have states that govern allowed operations
 * - State transitions must follow business rules
 * - Invalid state changes can corrupt business processes
 * - Clear error messages help users understand constraints
 * 
 * @author Claim Management Team
 */
public class InvalidClaimStateException extends RuntimeException {

    /**
     * Serial version UID for serialization compatibility
     */
    private static final long serialVersionUID = 1L;

    /**
     * The current status of the claim when the invalid operation was attempted
     */
    private final ClaimStatus currentStatus;

    /**
     * The attempted new status (if applicable)
     */
    private final ClaimStatus attemptedStatus;

    /**
     * The claim identifier associated with this exception
     */
    private final String claimIdentifier;

    /**
     * Default constructor with message only
     * 
     * @param message Descriptive error message
     */
    public InvalidClaimStateException(String message) {
        super(message);
        this.currentStatus = null;
        this.attemptedStatus = null;
        this.claimIdentifier = null;
    }

    /**
     * Constructor with message and current status
     * 
     * @param message Descriptive error message
     * @param currentStatus The current status of the claim
     */
    public InvalidClaimStateException(String message, ClaimStatus currentStatus) {
        super(message);
        this.currentStatus = currentStatus;
        this.attemptedStatus = null;
        this.claimIdentifier = null;
    }

    /**
     * Constructor with message, current status, and attempted status
     * 
     * @param message Descriptive error message
     * @param currentStatus The current status of the claim
     * @param attemptedStatus The status that was attempted to be set
     */
    public InvalidClaimStateException(String message, ClaimStatus currentStatus, ClaimStatus attemptedStatus) {
        super(message);
        this.currentStatus = currentStatus;
        this.attemptedStatus = attemptedStatus;
        this.claimIdentifier = null;
    }

    /**
     * Full constructor with all details
     * 
     * @param message Descriptive error message
     * @param currentStatus The current status of the claim
     * @param attemptedStatus The status that was attempted to be set
     * @param claimIdentifier The claim identifier
     */
    public InvalidClaimStateException(String message, ClaimStatus currentStatus, 
                                    ClaimStatus attemptedStatus, String claimIdentifier) {
        super(message);
        this.currentStatus = currentStatus;
        this.attemptedStatus = attemptedStatus;
        this.claimIdentifier = claimIdentifier;
    }

    /**
     * Constructor with message and cause
     * 
     * @param message Descriptive error message
     * @param cause The underlying cause of this exception
     */
    public InvalidClaimStateException(String message, Throwable cause) {
        super(message, cause);
        this.currentStatus = null;
        this.attemptedStatus = null;
        this.claimIdentifier = null;
    }

    /**
     * Gets the current status of the claim
     * 
     * @return The current claim status, or null if not provided
     */
    public ClaimStatus getCurrentStatus() {
        return currentStatus;
    }

    /**
     * Gets the attempted status that caused the exception
     * 
     * @return The attempted status, or null if not applicable
     */
    public ClaimStatus getAttemptedStatus() {
        return attemptedStatus;
    }

    /**
     * Gets the claim identifier
     * 
     * @return The claim identifier, or null if not provided
     */
    public String getClaimIdentifier() {
        return claimIdentifier;
    }

    /**
     * Factory method for invalid status transition
     * 
     * @param currentStatus The current status of the claim
     * @param attemptedStatus The status that was attempted
     * @param claimIdentifier The claim identifier
     * @return InvalidClaimStateException with standardized message
     */
    public static InvalidClaimStateException invalidTransition(ClaimStatus currentStatus, 
                                                             ClaimStatus attemptedStatus, 
                                                             String claimIdentifier) {
        String message = String.format(
            "Invalid status transition from %s to %s for claim %s", 
            currentStatus, attemptedStatus, claimIdentifier
        );
        return new InvalidClaimStateException(message, currentStatus, attemptedStatus, claimIdentifier);
    }

    /**
     * Factory method for terminal state modification attempt
     * 
     * @param currentStatus The terminal status of the claim
     * @param claimIdentifier The claim identifier
     * @return InvalidClaimStateException with standardized message
     */
    public static InvalidClaimStateException terminalStateModification(ClaimStatus currentStatus, 
                                                                     String claimIdentifier) {
        String message = String.format(
            "Cannot modify claim %s in terminal state: %s", 
            claimIdentifier, currentStatus
        );
        return new InvalidClaimStateException(message, currentStatus, null, claimIdentifier);
    }

    /**
     * Factory method for operation not allowed in current state
     * 
     * @param operation The operation that was attempted
     * @param currentStatus The current status of the claim
     * @param claimIdentifier The claim identifier
     * @return InvalidClaimStateException with standardized message
     */
    public static InvalidClaimStateException operationNotAllowed(String operation, 
                                                               ClaimStatus currentStatus, 
                                                               String claimIdentifier) {
        String message = String.format(
            "Operation '%s' not allowed for claim %s in status: %s", 
            operation, claimIdentifier, currentStatus
        );
        return new InvalidClaimStateException(message, currentStatus, null, claimIdentifier);
    }

    /**
     * Checks if this exception represents a status transition error
     * 
     * @return true if this is a status transition error
     */
    public boolean isStatusTransitionError() {
        return currentStatus != null && attemptedStatus != null;
    }

    /**
     * Checks if this exception represents a terminal state error
     * 
     * @return true if this is a terminal state error
     */
    public boolean isTerminalStateError() {
        return currentStatus != null && currentStatus.isTerminal();
    }

    /**
     * Gets a detailed error description including status information
     * 
     * @return Detailed error description
     */
    public String getDetailedMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(getMessage());
        
        if (claimIdentifier != null) {
            sb.append(" [Claim: ").append(claimIdentifier).append("]");
        }
        
        if (currentStatus != null) {
            sb.append(" [Current Status: ").append(currentStatus).append("]");
        }
        
        if (attemptedStatus != null) {
            sb.append(" [Attempted Status: ").append(attemptedStatus).append("]");
        }
        
        return sb.toString();
    }

    /**
     * Override toString to provide comprehensive information
     * 
     * @return String representation of the exception
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + getDetailedMessage();
    }
}