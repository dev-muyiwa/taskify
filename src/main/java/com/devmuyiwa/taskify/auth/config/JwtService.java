package com.devmuyiwa.taskify.auth.config;

import com.devmuyiwa.taskify.user.UserRepository;
import com.devmuyiwa.taskify.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {
    
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    
    public String extractUsername(String token) {
        try {
            UUID userId = jwtUtil.extractUserId(token);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BadCredentialsException("User not found"));
            return user.getEmail();
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid JWT token: " + e.getMessage());
        }
    }
    
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            UUID userId = jwtUtil.extractUserId(token);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BadCredentialsException("User not found"));
            
            if (!user.getEmail().equals(userDetails.getUsername())) {
                throw new BadCredentialsException("Token username mismatch");
            }
            
            if (!jwtUtil.isValid(token)) {
                throw new BadCredentialsException("Invalid JWT token");
            }
            
            return true;
        } catch (BadCredentialsException e) {
            throw e; // Re-throw authentication exceptions
        } catch (Exception e) {
            throw new BadCredentialsException("JWT validation failed: " + e.getMessage());
        }
    }
}
