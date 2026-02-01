package com.claimmanagement.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler for the Claim Management Service
 * 
 * @RestControllerAdvice combines @ControllerAdvice and @ResponseBody:
 * - @ControllerAdvice: Makes this class handle exceptions globally across all controllers
 * - @ResponseBody: Automatically serializes return values to JSON
 * 
 * Benefits of Global Exception Handling:
 * 1. Centralized Error Handling: All exceptions handled in one place
 * 2. Consistent Error Response Format: Standardized error structure
 * 3. Separation of Concerns: Controllers focus on business logic, not error handling
 * 4. Reduced Code Duplication: No need to handle exceptions in each controller
 * 5. Better Logging: Centralized logging of all exceptions
 * 6. Client-Friendly Responses: Convert technical exceptions to user-friendly messages
 * 
 * Exception Handling Strategy:
 * - Business exceptions (404, 400) return appropriate HTTP status codes
 * - Technical exceptions (500) are logged and return generic error messages
 * - Validation errors are formatted as field-specific error maps
 * - All responses include timestamp and request path for debugging
 * 
 * @author Claim Management Team
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Logger for exception handling
     */
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles ClaimNotFoundException
     * 
     * @ExceptionHandler specifies which exception types this method handles
     * Returns HTTP 404 (Not Found) for business logic "not found" scenarios
     * 
     * @param ex The ClaimNotFoundException that was thrown
     * @param request The web request context
     * @return ResponseEntity with error details and 404 status
     */
    @ExceptionHandler(ClaimNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleClaimNotFoundException(
            ClaimNotFoundException ex, WebRequest request) {
        
        logger.warn("Claim not found: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Claim Not Found")
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        // Add claim identifier if available
        if (ex.getClaimIdentifier() != null) {
            errorResponse.addDetail("claimIdentifier", ex.getClaimIdentifier());
        }
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles InvalidClaimStateException
     * 
     * Returns HTTP 400 (Bad Request) for invalid business operations
     * 
     * @param ex The InvalidClaimStateException that was thrown
     * @param request The web request context
     * @return ResponseEntity with error details and 400 status
     */
    @ExceptionHandler(InvalidClaimStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidClaimStateException(
            InvalidClaimStateException ex, WebRequest request) {
        
        logger.warn("Invalid claim state operation: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Invalid Claim State")
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        // Add status information if available
        if (ex.getCurrentStatus() != null) {
            errorResponse.addDetail("currentStatus", ex.getCurrentStatus().toString());
        }
        if (ex.getAttemptedStatus() != null) {
            errorResponse.addDetail("attemptedStatus", ex.getAttemptedStatus().toString());
        }
        if (ex.getClaimIdentifier() != null) {
            errorResponse.addDetail("claimIdentifier", ex.getClaimIdentifier());
        }
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles Bean Validation Errors
     * 
     * MethodArgumentNotValidException is thrown when @Valid annotation fails
     * This occurs when request DTOs don't pass validation constraints
     * 
     * Returns HTTP 400 (Bad Request) with detailed field-level validation errors
     * 
     * @param ex The MethodArgumentNotValidException containing validation errors
     * @param request The web request context
     * @return ResponseEntity with validation error details and 400 status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        logger.warn("Validation failed: {} validation errors", ex.getBindingResult().getErrorCount());
        
        // Extract field-level validation errors
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Request validation failed")
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        // Add validation errors as details
        errorResponse.addDetail("validationErrors", validationErrors);
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles IllegalArgumentException
     * 
     * Often thrown by business logic for invalid input parameters
     * Returns HTTP 400 (Bad Request)
     * 
     * @param ex The IllegalArgumentException that was thrown
     * @param request The web request context
     * @return ResponseEntity with error details and 400 status
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        logger.warn("Illegal argument: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Invalid Request")
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles IllegalStateException
     * 
     * Thrown when an operation is called at an inappropriate time
     * Returns HTTP 409 (Conflict)
     * 
     * @param ex The IllegalStateException that was thrown
     * @param request The web request context
     * @return ResponseEntity with error details and 409 status
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(
            IllegalStateException ex, WebRequest request) {
        
        logger.warn("Illegal state: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Conflict")
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Handles all other RuntimeExceptions
     * 
     * Catch-all handler for unexpected runtime exceptions
     * Returns HTTP 500 (Internal Server Error)
     * Logs full stack trace for debugging
     * 
     * @param ex The RuntimeException that was thrown
     * @param request The web request context
     * @return ResponseEntity with generic error message and 500 status
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        
        logger.error("Unexpected runtime exception", ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred. Please try again later.")
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        // In development, include exception details
        // In production, this should be removed for security
        if (isDevelopmentMode()) {
            errorResponse.addDetail("exceptionType", ex.getClass().getSimpleName());
            errorResponse.addDetail("exceptionMessage", ex.getMessage());
        }
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles all other Exceptions (checked exceptions)
     * 
     * Final catch-all handler for any exceptions not handled above
     * Returns HTTP 500 (Internal Server Error)
     * 
     * @param ex The Exception that was thrown
     * @param request The web request context
     * @return ResponseEntity with generic error message and 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {
        
        logger.error("Unexpected exception", ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred. Please contact support.")
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Checks if the application is running in development mode
     * 
     * In a real application, this would check Spring profiles or environment variables
     * 
     * @return true if in development mode
     */
    private boolean isDevelopmentMode() {
        // Simple check - in real applications, use Spring profiles
        return true; // Set to false in production
    }

    /**
     * Error Response DTO for consistent error format
     * 
     * Provides a standardized structure for all error responses
     * Includes common fields and allows for additional details
     */
    public static class ErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        private Map<String, Object> details;

        // Builder pattern for easy construction
        public static ErrorResponseBuilder builder() {
            return new ErrorResponseBuilder();
        }

        // Constructors
        public ErrorResponse() {
            this.details = new HashMap<>();
        }

        public ErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path) {
            this.timestamp = timestamp;
            this.status = status;
            this.error = error;
            this.message = message;
            this.path = path;
            this.details = new HashMap<>();
        }

        // Method to add additional details
        public void addDetail(String key, Object value) {
            if (details == null) {
                details = new HashMap<>();
            }
            details.put(key, value);
        }

        // Getters and Setters
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

        public int getStatus() { return status; }
        public void setStatus(int status) { this.status = status; }

        public String getError() { return error; }
        public void setError(String error) { this.error = error; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }

        public Map<String, Object> getDetails() { return details; }
        public void setDetails(Map<String, Object> details) { this.details = details; }

        // Builder class
        public static class ErrorResponseBuilder {
            private LocalDateTime timestamp;
            private int status;
            private String error;
            private String message;
            private String path;

            public ErrorResponseBuilder timestamp(LocalDateTime timestamp) {
                this.timestamp = timestamp;
                return this;
            }

            public ErrorResponseBuilder status(int status) {
                this.status = status;
                return this;
            }

            public ErrorResponseBuilder error(String error) {
                this.error = error;
                return this;
            }

            public ErrorResponseBuilder message(String message) {
                this.message = message;
                return this;
            }

            public ErrorResponseBuilder path(String path) {
                this.path = path;
                return this;
            }

            public ErrorResponse build() {
                return new ErrorResponse(timestamp, status, error, message, path);
            }
        }
    }
}