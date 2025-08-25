package com.devmuyiwa.taskify.user;

import com.devmuyiwa.taskify.auth.dto.req.RegisterRequest;
import com.devmuyiwa.taskify.user.domain.User;
import com.devmuyiwa.taskify.user.dto.UserResponse;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Timed(value = "user.create", description = "Time taken to create a new user")
    public User createUser(RegisterRequest request) {
        if (userRepo.existsByEmail(request.email().toLowerCase())) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }

        User user = User.builder()
                .firstName(request.firstName().trim())
                .lastName(request.lastName().trim())
                .email(request.email().toLowerCase().trim())
                .password(passwordEncoder.encode(request.password()))
                .hasAcceptedTerms(request.hasAcceptedTerms())
                .build();

        return userRepo.save(user);
    }

    public Optional<User> findByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank.");
        }
        String normalizedEmail = email.toLowerCase().trim();

        return userRepo.findByEmail(normalizedEmail);
    }

    public Optional<User> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null or blank.");
        }
        return userRepo.findById(id);
    }

    public void updatePassword(User user, String newPassword) {
        if (user == null || newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("User and new password cannot be null or blank.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }

    @Transactional
    public void markEmailAsVerified(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        
        // Make it idempotent - only update if not already verified
        if (user.getEmailVerifiedAt() == null) {
            user.setEmailVerifiedAt(Instant.now());
            userRepo.save(user);
        }
        // If already verified, do nothing (idempotent)
    }

    public UserResponse getCurrentUser(UUID userId) {
        User user = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return new UserResponse(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getEmailVerifiedAt(),
            user.getCreatedAt()
        );
    }
}
