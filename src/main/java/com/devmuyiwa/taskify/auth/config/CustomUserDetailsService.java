package com.devmuyiwa.taskify.auth.config;

import com.devmuyiwa.taskify.auth.util.AuthUser;
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

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("Loading user by username: " + email);
        return userService.findByEmail(email)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    private UserDetails createUserDetails(User user) {
        System.out.println("Creating UserDetails for user: " + user.getEmail());
        System.out.println("Stored password hash: " + user.getPassword());
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities("USER")
                .build();
    }

    public AuthUser loadUserById(UUID userId) throws UsernameNotFoundException {
        return userService.findById(userId)
                .map(user -> new AuthUser(user.getId()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
    }
} 