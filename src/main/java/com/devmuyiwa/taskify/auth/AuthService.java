package com.devmuyiwa.taskify.auth;

import com.devmuyiwa.taskify.auth.config.JwtUtil;
import com.devmuyiwa.taskify.auth.dto.req.LoginRequest;
import com.devmuyiwa.taskify.auth.dto.req.RegisterRequest;
import com.devmuyiwa.taskify.auth.dto.res.AuthResponse;
import com.devmuyiwa.taskify.auth.util.AuthUser;
import com.devmuyiwa.taskify.common.events.UserRegisteredEvent;
import com.devmuyiwa.taskify.user.UserService;
import com.devmuyiwa.taskify.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (!req.termsAccepted()) {
            throw new IllegalArgumentException("You must accept the terms and conditions to register.");
        }

        User user = userService.createUser(req);

        eventPublisher.publishEvent(new UserRegisteredEvent(
                req.firstName().trim(), req.lastName().trim(), user
        ));

        String token = jwtUtil.generateToken(user.getId());
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest req) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.email().trim(), req.password())
            );
            AuthUser authUser = (AuthUser) auth.getPrincipal();
            String token = jwtUtil.generateToken(authUser.id());

            return new AuthResponse(token);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid email or password.");
        }
    }
}
