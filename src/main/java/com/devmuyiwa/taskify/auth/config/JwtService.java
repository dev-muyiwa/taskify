package com.devmuyiwa.taskify.auth.config;

import com.devmuyiwa.taskify.auth.exception.JwtAuthenticationException;
import com.devmuyiwa.taskify.user.UserRepository;
import com.devmuyiwa.taskify.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
    
    final JwtUtil jwtUtil;
    final UserRepository userRepository;
    
    public String extractUsername(String token) {
        try {
            UUID userId = jwtUtil.extractUserId(token);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new JwtAuthenticationException("User not found for token", JwtAuthenticationException.JwtErrorType.USER_NOT_FOUND));
            return user.getEmail();
        } catch (JwtAuthenticationException e) {
            // Re-throw JWT authentication exceptions
            throw e;
        } catch (Exception e) {
            log.error("Error extracting username from JWT token: {}", e.getMessage());
            throw new JwtAuthenticationException("Invalid JWT token format", JwtAuthenticationException.JwtErrorType.INVALID_FORMAT, e);
        }
    }

    public UUID extractUserId(String token) {
        try {
            return jwtUtil.extractUserId(token);
        } catch (JwtAuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error extracting userId from JWT token: {}", e.getMessage());
            throw new JwtAuthenticationException("Invalid JWT token format", JwtAuthenticationException.JwtErrorType.INVALID_FORMAT, e);
        }
    }

    // New: validate token only (signature/expiration), no DB lookups
    public boolean isTokenValid(String token) {
        try {
            return jwtUtil.isValid(token);
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    // Kept for backward compatibility where needed elsewhere
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            if (!jwtUtil.isValid(token)) {
                log.warn("JWT token structure validation failed");
                return false;
            }
            // DB checks are intentionally avoided during filter auth flow.
            return true;
        } catch (Exception e) {
            log.error("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }
}
