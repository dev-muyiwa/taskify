package com.devmuyiwa.taskify.common.exception;

import com.devmuyiwa.taskify.auth.exception.JwtAuthenticationException;
import com.devmuyiwa.taskify.common.dto.ApiErrorResponse;
import com.devmuyiwa.taskify.common.filter.RequestIdFilter;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private String getRequestId(HttpServletRequest request) {
        return Optional.ofNullable((String) request.getAttribute(RequestIdFilter.REQUEST_ID_HEADER)).orElse("N/A");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (msg1, msg2) -> msg1
                ));

        ApiErrorResponse response = ApiErrorResponse.builder()
                .message("Validation failed.")
                .error(errors)
                .requestId(getRequestId(request))
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgs(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        ApiErrorResponse response = ApiErrorResponse.builder()
                .message(ex.getMessage())
                .error(Map.of("error", ex.getMessage()))
                .requestId(getRequestId(request))
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleForbidden(AccessDeniedException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiErrorResponse.builder()
                .message("You do not have permission to access this resource.")
                .requestId(getRequestId(request))
                .path(request.getRequestURI())
                .error(null)
                .build());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiErrorResponse> handleExpiredJwt(ExpiredJwtException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiErrorResponse.builder()
                .message("Your session has expired. Please log in again.")
                .requestId(getRequestId(request))
                .path(request.getRequestURI())
                .error(Map.of("error", "JWT token expired", "details", ex.getMessage()))
                .build());
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ApiErrorResponse> handleMalformedJwt(MalformedJwtException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiErrorResponse.builder()
                .message("Invalid authentication token format.")
                .requestId(getRequestId(request))
                .path(request.getRequestURI())
                .error(Map.of("error", "Malformed JWT token", "details", ex.getMessage()))
                .build());
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<ApiErrorResponse> handleUnsupportedJwt(UnsupportedJwtException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiErrorResponse.builder()
                .message("Unsupported authentication token format.")
                .requestId(getRequestId(request))
                .path(request.getRequestURI())
                .error(Map.of("error", "Unsupported JWT token", "details", ex.getMessage()))
                .build());
    }

    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleJwtAuthentication(JwtAuthenticationException ex, HttpServletRequest request) {
        String message;
        String errorType = ex.getErrorType().name();
        
        switch (ex.getErrorType()) {
            case EXPIRED:
                message = "Your session has expired. Please log in again.";
                break;
            case MALFORMED:
                message = "Invalid authentication token format.";
                break;
            case UNSUPPORTED:
                message = "Unsupported authentication token format.";
                break;
            case SIGNATURE_INVALID:
                message = "Invalid authentication token signature.";
                break;
            case USER_NOT_FOUND:
                message = "User account not found or has been removed.";
                break;
            case USER_DISABLED:
                message = "User account is disabled.";
                break;
            case TOKEN_MISMATCH:
                message = "Authentication token mismatch.";
                break;
            case INVALID_FORMAT:
                message = "Invalid authentication token format.";
                break;
            default:
                message = "Authentication failed. Please check your credentials and try again.";
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiErrorResponse.builder()
                .message(message)
                .requestId(getRequestId(request))
                .path(request.getRequestURI())
                .error(Map.of("error", errorType, "details", ex.getMessage()))
                .build());
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
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiErrorResponse.builder()
                .message(message)
                .requestId(getRequestId(request))
                .path(request.getRequestURI())
                .error(Map.of("error", ex.getMessage()))
                .build());
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ApiErrorResponse> handleUnsupportedOperation(UnsupportedOperationException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiErrorResponse.builder()
                .message("Authentication required")
                .requestId(getRequestId(request))
                .path(request.getRequestURI())
                .error(Map.of("error", ex.getMessage()))
                .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        ApiErrorResponse response = ApiErrorResponse.builder()
                .message("An unexpected error occurred.")
                .error(Map.of("error", ex.getMessage()))
                .requestId(getRequestId(request))
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
