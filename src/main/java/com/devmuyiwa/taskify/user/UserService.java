package com.devmuyiwa.taskify.user;

import com.devmuyiwa.taskify.auth.dto.req.RegisterRequest;
import com.devmuyiwa.taskify.user.domain.User;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        if (userRepo.existsByEmailIgnoreCase(request.email())) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }

        User user = User.builder()
                .email(request.email().toLowerCase().trim())
                .password(passwordEncoder.encode(request.password()))
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
}
