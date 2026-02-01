package com.claimmanagement.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ClaimNotFoundException
 * 
 * Tests all constructors and methods to achieve 100% coverage
 * 
 * @author Claim Management Team
 */
@DisplayName("ClaimNotFoundException Tests")
class ClaimNotFoundExceptionTest {

    @Test
    @DisplayName("Should create exception with message only")
    void shouldCreateExceptionWithMessageOnly() {
        // Given
        String message = "Test exception message";
        
        // When
        ClaimNotFoundException exception = new ClaimNotFoundException(message);
        
        // Then
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getClaimIdentifier()).isNull();
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("Should create exception with message and claim identifier")
    void shouldCreateExceptionWithMessageAndClaimIdentifier() {
        // Given
        String message = "Test exception message";
        String claimIdentifier = "CLM-2024-001";
        
        // When
        ClaimNotFoundException exception = new ClaimNotFoundException(message, claimIdentifier);
        
        // Then
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getClaimIdentifier()).isEqualTo(claimIdentifier);
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("Should create exception with message and cause")
    void shouldCreateExceptionWithMessageAndCause() {
        // Given
        String message = "Test exception message";
        Throwable cause = new RuntimeException("Root cause");
        
        // When
        ClaimNotFoundException exception = new ClaimNotFoundException(message, cause);
        
        // Then
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getClaimIdentifier()).isNull();
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("Should create exception with message, claim identifier, and cause")
    void shouldCreateExceptionWithMessageClaimIdentifierAndCause() {
        // Given
        String message = "Test exception message";
        String claimIdentifier = "CLM-2024-001";
        Throwable cause = new RuntimeException("Root cause");
        
        // When
        ClaimNotFoundException exception = new ClaimNotFoundException(message, claimIdentifier, cause);
        
        // Then
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getClaimIdentifier()).isEqualTo(claimIdentifier);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("Should create exception by ID using factory method")
    void shouldCreateExceptionByIdUsingFactoryMethod() {
        // Given
        Long id = 123L;
        
        // When
        ClaimNotFoundException exception = ClaimNotFoundException.byId(id);
        
        // Then
        assertThat(exception.getMessage()).isEqualTo("Claim not found with ID: " + id);
        assertThat(exception.getClaimIdentifier()).isEqualTo(String.valueOf(id));
    }

    @Test
    @DisplayName("Should create exception by claim number using factory method")
    void shouldCreateExceptionByClaimNumberUsingFactoryMethod() {
        // Given
        String claimNumber = "CLM-2024-001";
        
        // When
        ClaimNotFoundException exception = ClaimNotFoundException.byClaimNumber(claimNumber);
        
        // Then
        assertThat(exception.getMessage()).isEqualTo("Claim not found with claim number: " + claimNumber);
        assertThat(exception.getClaimIdentifier()).isEqualTo(claimNumber);
    }

    @Test
    @DisplayName("Should create exception by policy number using factory method")
    void shouldCreateExceptionByPolicyNumberUsingFactoryMethod() {
        // Given
        String policyNumber = "POL-2024-001";
        
        // When
        ClaimNotFoundException exception = ClaimNotFoundException.byPolicyNumber(policyNumber);
        
        // Then
        assertThat(exception.getMessage()).isEqualTo("No claims found for policy number: " + policyNumber);
        assertThat(exception.getClaimIdentifier()).isEqualTo(policyNumber);
    }

    @Test
    @DisplayName("Should return proper toString representation")
    void shouldReturnProperToStringRepresentation() {
        // Given
        String message = "Test exception message";
        String claimIdentifier = "CLM-2024-001";
        ClaimNotFoundException exception = new ClaimNotFoundException(message, claimIdentifier);
        
        // When
        String toString = exception.toString();
        
        // Then
        assertThat(toString).contains("ClaimNotFoundException");
        assertThat(toString).contains(message);
        assertThat(toString).contains("Claim Identifier: " + claimIdentifier);
    }

    @Test
    @DisplayName("Should return toString without claim identifier when null")
    void shouldReturnToStringWithoutClaimIdentifierWhenNull() {
        // Given
        String message = "Test exception message";
        ClaimNotFoundException exception = new ClaimNotFoundException(message);
        
        // When
        String toString = exception.toString();
        
        // Then
        assertThat(toString).contains("ClaimNotFoundException");
        assertThat(toString).contains(message);
        assertThat(toString).doesNotContain("Claim Identifier:");
    }
}