package com.devmuyiwa.taskify.auth.config;

import com.devmuyiwa.taskify.auth.util.AuthUser;
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
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final HandlerExceptionResolver handlerExceptionResolver;

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

            if (!StringUtils.hasText(jwt)) {
                filterChain.doFilter(request, response);
                return;
            }

            if (!jwtService.isTokenValid(jwt)) {
                filterChain.doFilter(request, response);
                return;
            }

            final UUID userId = jwtService.extractUserId(jwt);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null) {
                AuthUser authUser = new AuthUser(userId);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        authUser,
                        null,
                        null
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            log.error("Unexpected error processing JWT token: {}", e.getMessage());
            handlerExceptionResolver.resolveException(request, response, null, e);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
