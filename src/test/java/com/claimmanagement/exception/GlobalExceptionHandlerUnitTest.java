package com.claimmanagement.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for GlobalExceptionHandler
 * 
 * @author Claim Management Team
 */
@DisplayName("Global Exception Handler Unit Tests")
class GlobalExceptionHandlerUnitTest {

    private GlobalExceptionHandler globalExceptionHandler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
        webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("uri=/test/path");
    }

    @Test
    @DisplayName("Should handle ClaimNotFoundException")
    void shouldHandleClaimNotFoundException() {
        // Arrange
        String errorMessage = "Claim not found with ID: 123";
        ClaimNotFoundException exception = new ClaimNotFoundException(errorMessage);

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
            globalExceptionHandler.handleClaimNotFoundException(exception, webRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getError()).isEqualTo("Claim Not Found");
        assertThat(response.getBody().getMessage()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("Should handle InvalidClaimStateException")
    void shouldHandleInvalidClaimStateException() {
        // Arrange
        String errorMessage = "Invalid claim state transition";
        InvalidClaimStateException exception = new InvalidClaimStateException(errorMessage);

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
            globalExceptionHandler.handleInvalidClaimStateException(exception, webRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getError()).isEqualTo("Invalid Claim State");
        assertThat(response.getBody().getMessage()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException")
    void shouldHandleMethodArgumentNotValidException() {
        // Arrange
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("claimRequest", "claimantName", "Name is required");
        FieldError fieldError2 = new FieldError("claimRequest", "claimAmount", "Amount must be positive");
        
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));
        
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
            globalExceptionHandler.handleValidationExceptions(exception, webRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getError()).isEqualTo("Validation Failed");
        
        @SuppressWarnings("unchecked")
        Map<String, String> validationErrors = (Map<String, String>) response.getBody().getDetails().get("validationErrors");
        assertThat(validationErrors).containsEntry("claimantName", "Name is required");
        assertThat(validationErrors).containsEntry("claimAmount", "Amount must be positive");
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException")
    void shouldHandleIllegalArgumentException() {
        // Arrange
        String errorMessage = "Invalid argument provided";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
            globalExceptionHandler.handleIllegalArgumentException(exception, webRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getError()).isEqualTo("Bad Request");
        assertThat(response.getBody().getMessage()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("Should handle generic Exception")
    void shouldHandleGenericException() {
        // Arrange
        String errorMessage = "Something went wrong";
        Exception exception = new Exception(errorMessage);

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
            globalExceptionHandler.handleGenericException(exception, webRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getError()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().getMessage()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("Should create ErrorResponse with builder")
    void shouldCreateErrorResponseWithBuilder() {
        // Act
        GlobalExceptionHandler.ErrorResponse errorResponse = GlobalExceptionHandler.ErrorResponse.builder()
            .status(404)
            .error("Test Error")
            .message("Test message")
            .path("/test/path")
            .build();

        // Assert
        assertThat(errorResponse.getStatus()).isEqualTo(404);
        assertThat(errorResponse.getError()).isEqualTo("Test Error");
        assertThat(errorResponse.getMessage()).isEqualTo("Test message");
        assertThat(errorResponse.getPath()).isEqualTo("/test/path");
        assertThat(errorResponse.getTimestamp()).isNotNull();
    }
}