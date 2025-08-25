package com.devmuyiwa.taskify.auth.config;

import com.devmuyiwa.taskify.auth.util.AuthUser;
import com.devmuyiwa.taskify.user.UserRepository;
import com.devmuyiwa.taskify.user.UserService;
import com.devmuyiwa.taskify.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepo.findByEmail(email.toLowerCase().trim())
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    private UserDetails createUserDetails(User user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities("USER")
                .build();
    }

    public AuthUser loadUserById(UUID userId) throws UsernameNotFoundException {
        return userRepo.findById(userId)
                .map(user -> new AuthUser(user.getId()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
    }
} 