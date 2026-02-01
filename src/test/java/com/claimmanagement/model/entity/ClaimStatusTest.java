package com.claimmanagement.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ClaimStatus enum
 * 
 * Tests all methods and transitions to achieve 100% coverage
 * 
 * @author Claim Management Team
 */
@DisplayName("ClaimStatus Tests")
class ClaimStatusTest {

    @Test
    @DisplayName("Should have correct descriptions for all statuses")
    void shouldHaveCorrectDescriptionsForAllStatuses() {
        assertThat(ClaimStatus.SUBMITTED.getDescription())
            .isEqualTo("Claim has been submitted and is awaiting initial review");
        assertThat(ClaimStatus.UNDER_REVIEW.getDescription())
            .isEqualTo("Claim is under review by claims department");
        assertThat(ClaimStatus.APPROVED.getDescription())
            .isEqualTo("Claim has been approved for payment");
        assertThat(ClaimStatus.REJECTED.getDescription())
            .isEqualTo("Claim has been rejected");
        assertThat(ClaimStatus.PAID.getDescription())
            .isEqualTo("Claim has been paid");
        assertThat(ClaimStatus.CANCELLED.getDescription())
            .isEqualTo("Claim has been cancelled");
    }

    @Test
    @DisplayName("Should allow valid transitions from SUBMITTED")
    void shouldAllowValidTransitionsFromSubmitted() {
        assertThat(ClaimStatus.SUBMITTED.canTransitionTo(ClaimStatus.UNDER_REVIEW)).isTrue();
        assertThat(ClaimStatus.SUBMITTED.canTransitionTo(ClaimStatus.CANCELLED)).isTrue();
        
        // Invalid transitions
        assertThat(ClaimStatus.SUBMITTED.canTransitionTo(ClaimStatus.APPROVED)).isFalse();
        assertThat(ClaimStatus.SUBMITTED.canTransitionTo(ClaimStatus.REJECTED)).isFalse();
        assertThat(ClaimStatus.SUBMITTED.canTransitionTo(ClaimStatus.PAID)).isFalse();
        assertThat(ClaimStatus.SUBMITTED.canTransitionTo(ClaimStatus.SUBMITTED)).isFalse();
    }

    @Test
    @DisplayName("Should allow valid transitions from UNDER_REVIEW")
    void shouldAllowValidTransitionsFromUnderReview() {
        assertThat(ClaimStatus.UNDER_REVIEW.canTransitionTo(ClaimStatus.APPROVED)).isTrue();
        assertThat(ClaimStatus.UNDER_REVIEW.canTransitionTo(ClaimStatus.REJECTED)).isTrue();
        assertThat(ClaimStatus.UNDER_REVIEW.canTransitionTo(ClaimStatus.CANCELLED)).isTrue();
        
        // Invalid transitions
        assertThat(ClaimStatus.UNDER_REVIEW.canTransitionTo(ClaimStatus.SUBMITTED)).isFalse();
        assertThat(ClaimStatus.UNDER_REVIEW.canTransitionTo(ClaimStatus.PAID)).isFalse();
        assertThat(ClaimStatus.UNDER_REVIEW.canTransitionTo(ClaimStatus.UNDER_REVIEW)).isFalse();
    }

    @Test
    @DisplayName("Should allow valid transitions from APPROVED")
    void shouldAllowValidTransitionsFromApproved() {
        assertThat(ClaimStatus.APPROVED.canTransitionTo(ClaimStatus.PAID)).isTrue();
        assertThat(ClaimStatus.APPROVED.canTransitionTo(ClaimStatus.CANCELLED)).isTrue();
        
        // Invalid transitions
        assertThat(ClaimStatus.APPROVED.canTransitionTo(ClaimStatus.SUBMITTED)).isFalse();
        assertThat(ClaimStatus.APPROVED.canTransitionTo(ClaimStatus.UNDER_REVIEW)).isFalse();
        assertThat(ClaimStatus.APPROVED.canTransitionTo(ClaimStatus.REJECTED)).isFalse();
        assertThat(ClaimStatus.APPROVED.canTransitionTo(ClaimStatus.APPROVED)).isFalse();
    }

    @Test
    @DisplayName("Should not allow transitions from terminal states")
    void shouldNotAllowTransitionsFromTerminalStates() {
        // REJECTED is terminal
        assertThat(ClaimStatus.REJECTED.canTransitionTo(ClaimStatus.SUBMITTED)).isFalse();
        assertThat(ClaimStatus.REJECTED.canTransitionTo(ClaimStatus.UNDER_REVIEW)).isFalse();
        assertThat(ClaimStatus.REJECTED.canTransitionTo(ClaimStatus.APPROVED)).isFalse();
        assertThat(ClaimStatus.REJECTED.canTransitionTo(ClaimStatus.PAID)).isFalse();
        assertThat(ClaimStatus.REJECTED.canTransitionTo(ClaimStatus.CANCELLED)).isFalse();
        assertThat(ClaimStatus.REJECTED.canTransitionTo(ClaimStatus.REJECTED)).isFalse();

        // PAID is terminal
        assertThat(ClaimStatus.PAID.canTransitionTo(ClaimStatus.SUBMITTED)).isFalse();
        assertThat(ClaimStatus.PAID.canTransitionTo(ClaimStatus.UNDER_REVIEW)).isFalse();
        assertThat(ClaimStatus.PAID.canTransitionTo(ClaimStatus.APPROVED)).isFalse();
        assertThat(ClaimStatus.PAID.canTransitionTo(ClaimStatus.REJECTED)).isFalse();
        assertThat(ClaimStatus.PAID.canTransitionTo(ClaimStatus.CANCELLED)).isFalse();
        assertThat(ClaimStatus.PAID.canTransitionTo(ClaimStatus.PAID)).isFalse();

        // CANCELLED is terminal
        assertThat(ClaimStatus.CANCELLED.canTransitionTo(ClaimStatus.SUBMITTED)).isFalse();
        assertThat(ClaimStatus.CANCELLED.canTransitionTo(ClaimStatus.UNDER_REVIEW)).isFalse();
        assertThat(ClaimStatus.CANCELLED.canTransitionTo(ClaimStatus.APPROVED)).isFalse();
        assertThat(ClaimStatus.CANCELLED.canTransitionTo(ClaimStatus.REJECTED)).isFalse();
        assertThat(ClaimStatus.CANCELLED.canTransitionTo(ClaimStatus.PAID)).isFalse();
        assertThat(ClaimStatus.CANCELLED.canTransitionTo(ClaimStatus.CANCELLED)).isFalse();
    }

    @Test
    @DisplayName("Should return correct valid next statuses from SUBMITTED")
    void shouldReturnCorrectValidNextStatusesFromSubmitted() {
        ClaimStatus[] validNextStatuses = ClaimStatus.SUBMITTED.getValidNextStatuses();
        
        assertThat(validNextStatuses).hasSize(2);
        assertThat(validNextStatuses).contains(ClaimStatus.UNDER_REVIEW, ClaimStatus.CANCELLED);
    }

    @Test
    @DisplayName("Should return correct valid next statuses from UNDER_REVIEW")
    void shouldReturnCorrectValidNextStatusesFromUnderReview() {
        ClaimStatus[] validNextStatuses = ClaimStatus.UNDER_REVIEW.getValidNextStatuses();
        
        assertThat(validNextStatuses).hasSize(3);
        assertThat(validNextStatuses).contains(ClaimStatus.APPROVED, ClaimStatus.REJECTED, ClaimStatus.CANCELLED);
    }

    @Test
    @DisplayName("Should return correct valid next statuses from APPROVED")
    void shouldReturnCorrectValidNextStatusesFromApproved() {
        ClaimStatus[] validNextStatuses = ClaimStatus.APPROVED.getValidNextStatuses();
        
        assertThat(validNextStatuses).hasSize(2);
        assertThat(validNextStatuses).contains(ClaimStatus.PAID, ClaimStatus.CANCELLED);
    }

    @Test
    @DisplayName("Should return empty array for terminal states")
    void shouldReturnEmptyArrayForTerminalStates() {
        assertThat(ClaimStatus.REJECTED.getValidNextStatuses()).isEmpty();
        assertThat(ClaimStatus.PAID.getValidNextStatuses()).isEmpty();
        assertThat(ClaimStatus.CANCELLED.getValidNextStatuses()).isEmpty();
    }

    @Test
    @DisplayName("Should correctly identify terminal states")
    void shouldCorrectlyIdentifyTerminalStates() {
        assertThat(ClaimStatus.REJECTED.isTerminal()).isTrue();
        assertThat(ClaimStatus.PAID.isTerminal()).isTrue();
        assertThat(ClaimStatus.CANCELLED.isTerminal()).isTrue();
        
        assertThat(ClaimStatus.SUBMITTED.isTerminal()).isFalse();
        assertThat(ClaimStatus.UNDER_REVIEW.isTerminal()).isFalse();
        assertThat(ClaimStatus.APPROVED.isTerminal()).isFalse();
    }

    @Test
    @DisplayName("Should correctly identify successful status")
    void shouldCorrectlyIdentifySuccessfulStatus() {
        assertThat(ClaimStatus.PAID.isSuccessful()).isTrue();
        
        assertThat(ClaimStatus.SUBMITTED.isSuccessful()).isFalse();
        assertThat(ClaimStatus.UNDER_REVIEW.isSuccessful()).isFalse();
        assertThat(ClaimStatus.APPROVED.isSuccessful()).isFalse();
        assertThat(ClaimStatus.REJECTED.isSuccessful()).isFalse();
        assertThat(ClaimStatus.CANCELLED.isSuccessful()).isFalse();
    }

    @Test
    @DisplayName("Should return enum name as string representation")
    void shouldReturnEnumNameAsStringRepresentation() {
        assertThat(ClaimStatus.SUBMITTED.toString()).isEqualTo("SUBMITTED");
        assertThat(ClaimStatus.UNDER_REVIEW.toString()).isEqualTo("UNDER_REVIEW");
        assertThat(ClaimStatus.APPROVED.toString()).isEqualTo("APPROVED");
        assertThat(ClaimStatus.REJECTED.toString()).isEqualTo("REJECTED");
        assertThat(ClaimStatus.PAID.toString()).isEqualTo("PAID");
        assertThat(ClaimStatus.CANCELLED.toString()).isEqualTo("CANCELLED");
    }

    @Test
    @DisplayName("Should have all expected enum values")
    void shouldHaveAllExpectedEnumValues() {
        ClaimStatus[] values = ClaimStatus.values();
        
        assertThat(values).hasSize(6);
        assertThat(values).contains(
            ClaimStatus.SUBMITTED,
            ClaimStatus.UNDER_REVIEW,
            ClaimStatus.APPROVED,
            ClaimStatus.REJECTED,
            ClaimStatus.PAID,
            ClaimStatus.CANCELLED
        );
    }

    @Test
    @DisplayName("Should support valueOf operation")
    void shouldSupportValueOfOperation() {
        assertThat(ClaimStatus.valueOf("SUBMITTED")).isEqualTo(ClaimStatus.SUBMITTED);
        assertThat(ClaimStatus.valueOf("UNDER_REVIEW")).isEqualTo(ClaimStatus.UNDER_REVIEW);
        assertThat(ClaimStatus.valueOf("APPROVED")).isEqualTo(ClaimStatus.APPROVED);
        assertThat(ClaimStatus.valueOf("REJECTED")).isEqualTo(ClaimStatus.REJECTED);
        assertThat(ClaimStatus.valueOf("PAID")).isEqualTo(ClaimStatus.PAID);
        assertThat(ClaimStatus.valueOf("CANCELLED")).isEqualTo(ClaimStatus.CANCELLED);
    }
}