package com.devmuyiwa.taskify.common.exception;

import com.devmuyiwa.taskify.auth.exception.JwtAuthenticationException;
import com.devmuyiwa.taskify.common.dto.ApiErrorResponse;
import com.devmuyiwa.taskify.common.dto.ApiResponseBuilder;
import com.devmuyiwa.taskify.common.filter.RequestIdFilter;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
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
                HttpStatus.BAD_REQUEST,
                "INVALID_ARGUMENT",
                ex.getMessage(),
                "The provided argument is invalid",
                "VALIDATION_ERROR",
                Map.of("argument", ex.getMessage()),
                null,
                Map.of("errorCategory", "ARGUMENT_VALIDATION")
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleForbidden(AccessDeniedException ex, HttpServletRequest request) {
        ApiErrorResponse response = responseBuilder.authorizationError(
                "ACCESS_DENIED",
                "You do not have permission to access this resource",
                "The requested resource requires specific permissions that you do not have"
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiErrorResponse> handleExpiredJwt(ExpiredJwtException ex, HttpServletRequest request) {
        ApiErrorResponse response = responseBuilder.authenticationError(
                "JWT_EXPIRED",
                "Your session has expired. Please log in again.",
                "The JWT token provided has exceeded its expiration time"
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ApiErrorResponse> handleMalformedJwt(MalformedJwtException ex, HttpServletRequest request) {
        ApiErrorResponse response = responseBuilder.authenticationError(
                "JWT_MALFORMED",
                "Invalid authentication token format.",
                "The JWT token structure is invalid or corrupted"
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<ApiErrorResponse> handleUnsupportedJwt(UnsupportedJwtException ex, HttpServletRequest request) {
        ApiErrorResponse response = responseBuilder.authenticationError(
                "JWT_UNSUPPORTED",
                "Unsupported authentication token format.",
                "The JWT token uses an unsupported algorithm or format"
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleJwtAuthentication(JwtAuthenticationException ex, HttpServletRequest request) {
        String errorCode = "JWT_" + ex.getErrorType().name();
        String message = getJwtErrorMessage(ex.getErrorType());
        String detail = getJwtErrorDetail(ex.getErrorType());

        ApiErrorResponse response = responseBuilder.authenticationError(
                errorCode,
                message,
                detail
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<ApiErrorResponse> handleAuthentication(Exception ex, HttpServletRequest request) {
        String errorCode = "AUTH_FAILED";
        String message = "Authentication failed. Please check your credentials and try again.";
        String detail = "The provided credentials are invalid or insufficient for authentication";

        // Provide more specific messages for common authentication failures
        if (ex instanceof BadCredentialsException) {
            if (ex.getMessage().contains("expired")) {
                errorCode = "AUTH_EXPIRED";
                message = "Your session has expired. Please log in again.";
                detail = "The authentication session has exceeded its time limit";
            } else if (ex.getMessage().contains("signature")) {
                errorCode = "AUTH_SIGNATURE_INVALID";
                message = "Invalid authentication token signature.";
                detail = "The token signature verification failed";
            } else if (ex.getMessage().contains("malformed")) {
                errorCode = "AUTH_MALFORMED";
                message = "Invalid authentication token format.";
                detail = "The token structure is invalid";
            } else if (ex.getMessage().contains("User not found")) {
                errorCode = "USER_NOT_FOUND";
                message = "User account not found or has been removed.";
                detail = "The user account associated with the credentials does not exist";
            }
        }

        ApiErrorResponse response = responseBuilder.authenticationError(
                errorCode,
                message,
                detail
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ApiErrorResponse> handleUnsupportedOperation(UnsupportedOperationException ex, HttpServletRequest request) {
        ApiErrorResponse response = responseBuilder.error(
                HttpStatus.UNAUTHORIZED,
                "AUTH_REQUIRED",
                "Authentication required",
                "This operation requires valid authentication credentials",
                "AUTHENTICATION_ERROR",
                Map.of("operation", ex.getMessage()),
                null,
                Map.of("requiresAuth", true)
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        ApiErrorResponse response = responseBuilder.serverError(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred.",
                "The server encountered an internal error while processing your request"
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // Helper methods
    private ApiErrorResponse.FieldError mapToFieldError(FieldError fieldError) {
        return ApiErrorResponse.FieldError.builder()
                .field(fieldError.getField())
                .rejectedValue(fieldError.getRejectedValue())
                .message(fieldError.getDefaultMessage())
                .code(fieldError.getCode())
                .context(Map.of(
                        "objectName", fieldError.getObjectName(),
                        "field", fieldError.getField()
                ))
                .build();
    }

    private String getJwtErrorMessage(JwtAuthenticationException.JwtErrorType errorType) {
        return switch (errorType) {
            case EXPIRED -> "Your session has expired. Please log in again.";
            case MALFORMED, INVALID_FORMAT -> "Invalid authentication token format.";
            case UNSUPPORTED -> "Unsupported authentication token format.";
            case SIGNATURE_INVALID -> "Invalid authentication token signature.";
            case USER_NOT_FOUND -> "User account not found or has been removed.";
            case USER_DISABLED -> "User account is disabled.";
            case TOKEN_MISMATCH -> "Authentication token mismatch.";
        };
    }

    private String getJwtErrorDetail(JwtAuthenticationException.JwtErrorType errorType) {
        return switch (errorType) {
            case EXPIRED -> "The JWT token has exceeded its expiration time and is no longer valid.";
            case MALFORMED -> "The JWT token structure is invalid or corrupted and cannot be parsed.";
            case UNSUPPORTED -> "The JWT token uses an unsupported algorithm or format that this service cannot process.";
            case SIGNATURE_INVALID -> "The JWT token signature verification failed, indicating potential tampering.";
            case USER_NOT_FOUND -> "The user account associated with the token no longer exists in the system.";
            case USER_DISABLED -> "The user account associated with the token has been disabled or deactivated.";
            case TOKEN_MISMATCH -> "The token content does not match the expected user information.";
            case INVALID_FORMAT -> "The JWT token format is invalid and cannot be processed.";
        };
    }
}
