package com.claimmanagement.exception;

/**
 * Custom exception for when a claim is not found in the system
 * <p>
 * Custom Exceptions in Spring Boot:
 * 1. Provide meaningful error messages to clients
 * 2. Enable specific error handling logic
 * 3. Improve debugging and troubleshooting
 * 4. Allow for different HTTP status codes
 * 5. Support internationalization of error messages
 * <p>
 * Exception Hierarchy:
 * - RuntimeException: Unchecked exceptions (don't need to be declared)
 * - Checked exceptions: Must be declared in method signatures
 * - RuntimeException is preferred for business logic errors
 * <p>
 * Benefits of Custom Exceptions:
 * - Type Safety: Catch specific exception types
 * - Clarity: Clear indication of what went wrong
 * - Consistency: Standardized error handling across the application
 * - Flexibility: Can add custom fields and methods
 *
 * @author Claim Management Team
 */
public class ClaimNotFoundException extends RuntimeException {

    /**
     * Serial version UID for serialization
     * Important for maintaining compatibility across versions
     */
    private static final long serialVersionUID = 1L;

    /**
     * The ID or identifier of the claim that was not found
     * Useful for logging and debugging
     */
    private final String claimIdentifier;

    /**
     * Default constructor with message only
     *
     * @param message Descriptive error message
     */
    public ClaimNotFoundException(String message) {
        super(message);
        this.claimIdentifier = null;
    }

    /**
     * Constructor with message and claim identifier
     *
     * @param message         Descriptive error message
     * @param claimIdentifier The claim ID or number that was not found
     */
    public ClaimNotFoundException(String message, String claimIdentifier) {
        super(message);
        this.claimIdentifier = claimIdentifier;
    }

    /**
     * Constructor with message and cause
     * Useful when wrapping other exceptions
     *
     * @param message Descriptive error message
     * @param cause   The underlying cause of this exception
     */
    public ClaimNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.claimIdentifier = null;
    }

    /**
     * Constructor with message, claim identifier, and cause
     *
     * @param message         Descriptive error message
     * @param claimIdentifier The claim ID or number that was not found
     * @param cause           The underlying cause of this exception
     */
    public ClaimNotFoundException(String message, String claimIdentifier, Throwable cause) {
        super(message, cause);
        this.claimIdentifier = claimIdentifier;
    }

    /**
     * Gets the claim identifier that was not found
     *
     * @return The claim identifier, or null if not provided
     */
    public String getClaimIdentifier() {
        return claimIdentifier;
    }

    /**
     * Factory method for creating exception when claim not found by ID
     *
     * @param id The claim ID that was not found
     * @return ClaimNotFoundException with standardized message
     */
    public static ClaimNotFoundException byId(Long id) {
        return new ClaimNotFoundException(
                "Claim not found with ID: " + id,
                String.valueOf(id)
        );
    }

    /**
     * Factory method for creating exception when claim not found by claim number
     *
     * @param claimNumber The claim number that was not found
     * @return ClaimNotFoundException with standardized message
     */
    public static ClaimNotFoundException byClaimNumber(String claimNumber) {
        return new ClaimNotFoundException(
                "Claim not found with claim number: " + claimNumber,
                claimNumber
        );
    }

    /**
     * Factory method for creating exception when claim not found by policy number
     *
     * @param policyNumber The policy number for which no claims were found
     * @return ClaimNotFoundException with standardized message
     */
    public static ClaimNotFoundException byPolicyNumber(String policyNumber) {
        return new ClaimNotFoundException(
                "No claims found for policy number: " + policyNumber,
                policyNumber
        );
    }

    /**
     * Override toString to provide detailed information
     * Useful for logging and debugging
     *
     * @return String representation of the exception
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(": ");
        sb.append(getMessage());

        if (claimIdentifier != null) {
            sb.append(" [Claim Identifier: ").append(claimIdentifier).append("]");
        }

        return sb.toString();
    }
}