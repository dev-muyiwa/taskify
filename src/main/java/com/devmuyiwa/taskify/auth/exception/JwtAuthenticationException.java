package com.devmuyiwa.taskify.auth.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Custom exception for JWT authentication failures
 * This allows for more specific handling of JWT-related authentication issues
 */
public class JwtAuthenticationException extends AuthenticationException {
    
    private final JwtErrorType errorType;
    
    public JwtAuthenticationException(String message, JwtErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }
    
    public JwtAuthenticationException(String message, JwtErrorType errorType, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }
    
    public JwtErrorType getErrorType() {
        return errorType;
    }
    
    public enum JwtErrorType {
        EXPIRED,
        MALFORMED,
        UNSUPPORTED,
        SIGNATURE_INVALID,
        USER_NOT_FOUND,
        USER_DISABLED,
        TOKEN_MISMATCH,
        INVALID_FORMAT
    }
}
