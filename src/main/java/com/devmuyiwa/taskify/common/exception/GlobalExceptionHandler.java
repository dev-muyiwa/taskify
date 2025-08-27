package com.devmuyiwa.taskify.common.exception;

import com.devmuyiwa.taskify.auth.exception.AuthException;
import com.devmuyiwa.taskify.common.dto.ApiErrorResponse;
import com.devmuyiwa.taskify.common.dto.ApiResponseBuilder;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ApiResponseBuilder responseBuilder;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<ApiErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapToFieldError)
                .collect(Collectors.toList());

        ApiErrorResponse response = responseBuilder.validationError(
                "Validation failed for the provided data",
                fieldErrors
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgs(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        ApiErrorResponse response = responseBuilder.error(
                ex.getMessage(),
                null,
                null
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleForbidden(AccessDeniedException ex, HttpServletRequest request) {
        ApiErrorResponse response = responseBuilder.authorizationError(
                "You do not have permission to access this resource"
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }



    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthException(AuthException ex, HttpServletRequest request) {
        // Determine status code based on message content
        HttpStatus status = HttpStatus.UNAUTHORIZED; // Default
        
        if (ex.getMessage().contains("email already exists")) {
            status = HttpStatus.CONFLICT;
        } else if (ex.getMessage().contains("reset token") || ex.getMessage().contains("password")) {
            status = HttpStatus.BAD_REQUEST;
        }
        
        ApiErrorResponse response = responseBuilder.error(
                ex.getMessage(),
                null,
                null
        );
        
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<ApiErrorResponse> handleAuthentication(Exception ex, HttpServletRequest request) {
        String message = "Authentication failed. Please check your credentials and try again.";

        // Provide more specific messages for common authentication failures
        if (ex instanceof BadCredentialsException) {
            if (ex.getMessage().contains("expired")) {
                message = "Your session has expired. Please log in again.";
            } else if (ex.getMessage().contains("signature")) {
                message = "Invalid authentication token signature.";
            } else if (ex.getMessage().contains("malformed")) {
                message = "Invalid authentication token format.";
            } else if (ex.getMessage().contains("User not found")) {
                message = "User account not found or has been removed.";
            }
        }

        ApiErrorResponse response = responseBuilder.authenticationError(message);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ApiErrorResponse> handleUnsupportedOperation(UnsupportedOperationException ex, HttpServletRequest request) {
        ApiErrorResponse response = responseBuilder.error(
                "Authentication required",
                null,
                null
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        ApiErrorResponse response = responseBuilder.serverError("An unexpected error occurred.");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // Helper methods
    private ApiErrorResponse.FieldError mapToFieldError(FieldError fieldError) {
        return ApiErrorResponse.FieldError.builder()
                .field(fieldError.getField())
                .rejectedValue(fieldError.getRejectedValue())
                .message(fieldError.getDefaultMessage())
                .code(fieldError.getCode())
                .build();
    }

}
