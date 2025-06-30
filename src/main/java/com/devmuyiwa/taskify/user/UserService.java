package com.devmuyiwa.taskify.user;

import com.devmuyiwa.taskify.auth.dto.req.RegisterRequest;
import com.devmuyiwa.taskify.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(RegisterRequest request) {
        if (userRepo.existsByEmail(request.email())) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }

        User user = User.builder()
                .email(request.email().toLowerCase().trim())
                .password(passwordEncoder.encode(request.password()))
                .build();

        return userRepo.save(user);
    }
}
