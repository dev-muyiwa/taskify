package com.devmuyiwa.taskify.auth.config;

import com.devmuyiwa.taskify.auth.exception.JwtAuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        try {
            final String authHeader = request.getHeader("Authorization");

            if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            final String jwt = authHeader.substring(7);
            
            // Additional validation for JWT format
            if (!StringUtils.hasText(jwt)) {
                log.debug("Empty JWT token provided");
                filterChain.doFilter(request, response);
                return;
            }
            
            // Validate JWT format and extract user info
            final String userEmail = jwtService.extractUsername(jwt);
            
            if (userEmail == null) {
                log.warn("Failed to extract username from JWT token");
                filterChain.doFilter(request, response);
                return;
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null) {
                try {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                    if (jwtService.isTokenValid(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        log.debug("JWT authentication successful for user: {}", userEmail);
                    } else {
                        log.warn("JWT token validation failed for user: {}", userEmail);
                    }
                } catch (UsernameNotFoundException e) {
                    log.warn("User not found during JWT authentication: {}", userEmail);
                    // Don't throw exception here, just continue without authentication
                } catch (JwtAuthenticationException e) {
                    log.warn("JWT authentication exception for user {}: {}", userEmail, e.getMessage());
                    // Don't throw exception here, just continue without authentication
                }
            }
        } catch (JwtAuthenticationException e) {
            log.warn("JWT authentication failed: {}", e.getMessage());
            // Don't throw exception here, just continue without authentication
            // The GlobalExceptionHandler will handle any authentication failures
        } catch (Exception e) {
            log.error("Unexpected error processing JWT token: {}", e.getMessage());
            // Don't throw exception here, just continue without authentication
            // The GlobalExceptionHandler will handle any authentication failures
        }

        filterChain.doFilter(request, response);
    }
}
