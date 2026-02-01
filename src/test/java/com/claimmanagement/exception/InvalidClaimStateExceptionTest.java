package com.claimmanagement.exception;

import com.claimmanagement.model.entity.ClaimStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for InvalidClaimStateException
 * 
 * Tests all constructors and methods to achieve 100% coverage
 * 
 * @author Claim Management Team
 */
@DisplayName("InvalidClaimStateException Tests")
class InvalidClaimStateExceptionTest {

    @Test
    @DisplayName("Should create exception with message only")
    void shouldCreateExceptionWithMessageOnly() {
        // Given
        String message = "Invalid claim state";
        
        // When
        InvalidClaimStateException exception = new InvalidClaimStateException(message);
        
        // Then
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCurrentStatus()).isNull();
        assertThat(exception.getAttemptedStatus()).isNull();
        assertThat(exception.getClaimIdentifier()).isNull();
    }

    @Test
    @DisplayName("Should create exception with message and current status")
    void shouldCreateExceptionWithMessageAndCurrentStatus() {
        // Given
        String message = "Invalid claim state";
        ClaimStatus currentStatus = ClaimStatus.PAID;
        
        // When
        InvalidClaimStateException exception = new InvalidClaimStateException(message, currentStatus);
        
        // Then
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCurrentStatus()).isEqualTo(currentStatus);
        assertThat(exception.getAttemptedStatus()).isNull();
        assertThat(exception.getClaimIdentifier()).isNull();
    }

    @Test
    @DisplayName("Should create exception with message, current status, and attempted status")
    void shouldCreateExceptionWithMessageCurrentStatusAndAttemptedStatus() {
        // Given
        String message = "Invalid claim state";
        ClaimStatus currentStatus = ClaimStatus.PAID;
        ClaimStatus attemptedStatus = ClaimStatus.SUBMITTED;
        
        // When
        InvalidClaimStateException exception = new InvalidClaimStateException(message, currentStatus, attemptedStatus);
        
        // Then
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCurrentStatus()).isEqualTo(currentStatus);
        assertThat(exception.getAttemptedStatus()).isEqualTo(attemptedStatus);
        assertThat(exception.getClaimIdentifier()).isNull();
    }

    @Test
    @DisplayName("Should create exception with all parameters")
    void shouldCreateExceptionWithAllParameters() {
        // Given
        String message = "Invalid claim state";
        ClaimStatus currentStatus = ClaimStatus.PAID;
        ClaimStatus attemptedStatus = ClaimStatus.SUBMITTED;
        String claimIdentifier = "CLM-2024-001";
        
        // When
        InvalidClaimStateException exception = new InvalidClaimStateException(
            message, currentStatus, attemptedStatus, claimIdentifier);
        
        // Then
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCurrentStatus()).isEqualTo(currentStatus);
        assertThat(exception.getAttemptedStatus()).isEqualTo(attemptedStatus);
        assertThat(exception.getClaimIdentifier()).isEqualTo(claimIdentifier);
    }

    @Test
    @DisplayName("Should create exception with message and cause")
    void shouldCreateExceptionWithMessageAndCause() {
        // Given
        String message = "Invalid claim state";
        Throwable cause = new RuntimeException("Root cause");
        
        // When
        InvalidClaimStateException exception = new InvalidClaimStateException(message, cause);
        
        // Then
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getCurrentStatus()).isNull();
        assertThat(exception.getAttemptedStatus()).isNull();
        assertThat(exception.getClaimIdentifier()).isNull();
    }

    @Test
    @DisplayName("Should create invalid transition exception using factory method")
    void shouldCreateInvalidTransitionExceptionUsingFactoryMethod() {
        // Given
        ClaimStatus currentStatus = ClaimStatus.PAID;
        ClaimStatus attemptedStatus = ClaimStatus.SUBMITTED;
        String claimIdentifier = "CLM-2024-001";
        
        // When
        InvalidClaimStateException exception = InvalidClaimStateException.invalidTransition(
            currentStatus, attemptedStatus, claimIdentifier);
        
        // Then
        assertThat(exception.getMessage()).contains("Invalid status transition");
        assertThat(exception.getMessage()).contains(currentStatus.toString());
        assertThat(exception.getMessage()).contains(attemptedStatus.toString());
        assertThat(exception.getMessage()).contains(claimIdentifier);
        assertThat(exception.getCurrentStatus()).isEqualTo(currentStatus);
        assertThat(exception.getAttemptedStatus()).isEqualTo(attemptedStatus);
        assertThat(exception.getClaimIdentifier()).isEqualTo(claimIdentifier);
    }

    @Test
    @DisplayName("Should create terminal state modification exception using factory method")
    void shouldCreateTerminalStateModificationExceptionUsingFactoryMethod() {
        // Given
        ClaimStatus currentStatus = ClaimStatus.PAID;
        String claimIdentifier = "CLM-2024-001";
        
        // When
        InvalidClaimStateException exception = InvalidClaimStateException.terminalStateModification(
            currentStatus, claimIdentifier);
        
        // Then
        assertThat(exception.getMessage()).contains("Cannot modify claim");
        assertThat(exception.getMessage()).contains(claimIdentifier);
        assertThat(exception.getMessage()).contains("terminal state");
        assertThat(exception.getMessage()).contains(currentStatus.toString());
        assertThat(exception.getCurrentStatus()).isEqualTo(currentStatus);
        assertThat(exception.getAttemptedStatus()).isNull();
        assertThat(exception.getClaimIdentifier()).isEqualTo(claimIdentifier);
    }

    @Test
    @DisplayName("Should create operation not allowed exception using factory method")
    void shouldCreateOperationNotAllowedExceptionUsingFactoryMethod() {
        // Given
        String operation = "delete";
        ClaimStatus currentStatus = ClaimStatus.APPROVED;
        String claimIdentifier = "CLM-2024-001";
        
        // When
        InvalidClaimStateException exception = InvalidClaimStateException.operationNotAllowed(
            operation, currentStatus, claimIdentifier);
        
        // Then
        assertThat(exception.getMessage()).contains("Operation '" + operation + "' not allowed");
        assertThat(exception.getMessage()).contains(claimIdentifier);
        assertThat(exception.getMessage()).contains(currentStatus.toString());
        assertThat(exception.getCurrentStatus()).isEqualTo(currentStatus);
        assertThat(exception.getAttemptedStatus()).isNull();
        assertThat(exception.getClaimIdentifier()).isEqualTo(claimIdentifier);
    }

    @Test
    @DisplayName("Should identify status transition error")
    void shouldIdentifyStatusTransitionError() {
        // Given
        InvalidClaimStateException exception = new InvalidClaimStateException(
            "test", ClaimStatus.PAID, ClaimStatus.SUBMITTED, "CLM-001");
        
        // When & Then
        assertThat(exception.isStatusTransitionError()).isTrue();
    }

    @Test
    @DisplayName("Should not identify status transition error when attempted status is null")
    void shouldNotIdentifyStatusTransitionErrorWhenAttemptedStatusIsNull() {
        // Given
        InvalidClaimStateException exception = new InvalidClaimStateException(
            "test", ClaimStatus.PAID, null, "CLM-001");
        
        // When & Then
        assertThat(exception.isStatusTransitionError()).isFalse();
    }

    @Test
    @DisplayName("Should not identify status transition error when current status is null")
    void shouldNotIdentifyStatusTransitionErrorWhenCurrentStatusIsNull() {
        // Given
        InvalidClaimStateException exception = new InvalidClaimStateException(
            "test", null, ClaimStatus.SUBMITTED, "CLM-001");
        
        // When & Then
        assertThat(exception.isStatusTransitionError()).isFalse();
    }

    @Test
    @DisplayName("Should identify terminal state error")
    void shouldIdentifyTerminalStateError() {
        // Given
        InvalidClaimStateException exception = new InvalidClaimStateException(
            "test", ClaimStatus.PAID, null, "CLM-001");
        
        // When & Then
        assertThat(exception.isTerminalStateError()).isTrue();
    }

    @Test
    @DisplayName("Should not identify terminal state error for non-terminal status")
    void shouldNotIdentifyTerminalStateErrorForNonTerminalStatus() {
        // Given
        InvalidClaimStateException exception = new InvalidClaimStateException(
            "test", ClaimStatus.SUBMITTED, null, "CLM-001");
        
        // When & Then
        assertThat(exception.isTerminalStateError()).isFalse();
    }

    @Test
    @DisplayName("Should not identify terminal state error when current status is null")
    void shouldNotIdentifyTerminalStateErrorWhenCurrentStatusIsNull() {
        // Given
        InvalidClaimStateException exception = new InvalidClaimStateException("test");
        
        // When & Then
        assertThat(exception.isTerminalStateError()).isFalse();
    }

    @Test
    @DisplayName("Should return detailed message with all information")
    void shouldReturnDetailedMessageWithAllInformation() {
        // Given
        String message = "Test message";
        ClaimStatus currentStatus = ClaimStatus.PAID;
        ClaimStatus attemptedStatus = ClaimStatus.SUBMITTED;
        String claimIdentifier = "CLM-2024-001";
        
        InvalidClaimStateException exception = new InvalidClaimStateException(
            message, currentStatus, attemptedStatus, claimIdentifier);
        
        // When
        String detailedMessage = exception.getDetailedMessage();
        
        // Then
        assertThat(detailedMessage).contains(message);
        assertThat(detailedMessage).contains("Claim: " + claimIdentifier);
        assertThat(detailedMessage).contains("Current Status: " + currentStatus);
        assertThat(detailedMessage).contains("Attempted Status: " + attemptedStatus);
    }

    @Test
    @DisplayName("Should return detailed message with partial information")
    void shouldReturnDetailedMessageWithPartialInformation() {
        // Given
        String message = "Test message";
        ClaimStatus currentStatus = ClaimStatus.PAID;
        
        InvalidClaimStateException exception = new InvalidClaimStateException(message, currentStatus);
        
        // When
        String detailedMessage = exception.getDetailedMessage();
        
        // Then
        assertThat(detailedMessage).contains(message);
        assertThat(detailedMessage).contains("Current Status: " + currentStatus);
        assertThat(detailedMessage).doesNotContain("Claim:");
        assertThat(detailedMessage).doesNotContain("Attempted Status:");
    }

    @Test
    @DisplayName("Should return proper toString representation")
    void shouldReturnProperToStringRepresentation() {
        // Given
        String message = "Test message";
        ClaimStatus currentStatus = ClaimStatus.PAID;
        String claimIdentifier = "CLM-2024-001";
        
        InvalidClaimStateException exception = new InvalidClaimStateException(
            message, currentStatus, null, claimIdentifier);
        
        // When
        String toString = exception.toString();
        
        // Then
        assertThat(toString).contains("InvalidClaimStateException");
        assertThat(toString).contains(message);
        assertThat(toString).contains(claimIdentifier);
        assertThat(toString).contains(currentStatus.toString());
    }
}