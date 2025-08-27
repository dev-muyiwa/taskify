package com.devmuyiwa.taskify.auth.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Base class for all custom authentication-related exceptions.
 * The global exception handler should catch this type instead of each subclass.
 */
public class AuthException extends AuthenticationException {

    public AuthException() {
        super("Authentication error");
    }

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }
}


