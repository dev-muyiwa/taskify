package com.devmuyiwa.taskify.auth.config;

import com.devmuyiwa.taskify.auth.exception.JwtAuthenticationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long jwtExpirationInMs;

    private Key signingKey;

    @PostConstruct
    public void init() {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UUID userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateResetToken(UUID userId, Duration duration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + duration.toMillis());
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateEmailVerificationToken(UUID userId, int expirationMinutes) {
        Date now = new Date();
        // Convert minutes to milliseconds
        Date expiryDate = new Date(now.getTime() + (expirationMinutes * 60 * 1000));
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public UUID extractUserId(String token) {
        try {
            String subject = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

            return UUID.fromString(subject);
        } catch (ExpiredJwtException e) {
            log.warn("JWT token has expired: {}", e.getMessage());
            throw new JwtAuthenticationException("JWT token has expired", JwtAuthenticationException.JwtErrorType.EXPIRED, e);
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", e.getMessage());
            throw new JwtAuthenticationException("Unsupported JWT token format", JwtAuthenticationException.JwtErrorType.UNSUPPORTED, e);
        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT token: {}", e.getMessage());
            throw new JwtAuthenticationException("Malformed JWT token", JwtAuthenticationException.JwtErrorType.MALFORMED, e);
        } catch (SecurityException e) {
            log.warn("JWT signature validation failed: {}", e.getMessage());
            throw new JwtAuthenticationException("JWT signature validation failed", JwtAuthenticationException.JwtErrorType.SIGNATURE_INVALID, e);
        } catch (IllegalArgumentException e) {
            log.warn("JWT token is empty or null: {}", e.getMessage());
            throw new JwtAuthenticationException("JWT token is empty or null", JwtAuthenticationException.JwtErrorType.INVALID_FORMAT, e);
        } catch (Exception e) {
            log.error("Unexpected error parsing JWT token: {}", e.getMessage());
            throw new JwtAuthenticationException("JWT token parsing failed", JwtAuthenticationException.JwtErrorType.INVALID_FORMAT, e);
        }
    }

    public boolean isValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.debug("JWT token validation failed - expired: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            log.debug("JWT token validation failed - unsupported: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.debug("JWT token validation failed - malformed: {}", e.getMessage());
            return false;
        } catch (SecurityException e) {
            log.debug("JWT token validation failed - signature: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.debug("JWT token validation failed - invalid: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.debug("JWT token validation failed - unexpected: {}", e.getMessage());
            return false;
        }
    }
}

