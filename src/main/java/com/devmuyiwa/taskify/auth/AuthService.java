package com.devmuyiwa.taskify.auth;

import com.devmuyiwa.taskify.auth.config.JwtUtil;
import com.devmuyiwa.taskify.auth.dto.req.*;
import com.devmuyiwa.taskify.auth.dto.res.AuthResponse;
import com.devmuyiwa.taskify.auth.exception.AuthException;
import com.devmuyiwa.taskify.common.events.PasswordResetTokenGeneratedEvent;
import com.devmuyiwa.taskify.common.events.SuccessfulPasswordResetEvent;
import com.devmuyiwa.taskify.common.events.UserRegisteredEvent;
import com.devmuyiwa.taskify.user.UserService;
import com.devmuyiwa.taskify.user.domain.User;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;
    private final ApplicationEventPublisher eventPublisher;
    private final RedisTemplate<String, String> redisTemplate;
    private final MeterRegistry meterRegistry;
    private final Executor virtualThreadExecutor;

    @Transactional
    @Timed(value = "auth.register", description = "Time taken to register a new user")
    public AuthResponse register(RegisterRequest req, String requestId) {
        User user = userService.createUser(req);

        String token = jwtUtil.generateToken(user.getId());

        sendVerificationEmail(user, requestId);

        return new AuthResponse(token);
    }

    @Async("taskExecutor")
    protected void publishUserRegisteredEventAsync(User user, String verificationToken, int expirationMinutes, String requestId) {
        try {
            eventPublisher.publishEvent(
                    new UserRegisteredEvent(
                            user.getFirstName(),
                            user.getLastName(),
                            user.getEmail(),
                            verificationToken,
                            expirationMinutes,
                            requestId
                    )
            );
            log.info("UserRegisteredEvent published successfully for user: {}", user.getId());
        } catch (Exception e) {
            meterRegistry.counter("auth.register.userRegisteredEventError").increment();
            log.error("Failed to publish UserRegisteredEvent for user: {}", user.getId(), e);
        }
    }

    @Timed(value = "auth.login", description = "Time taken to login a user")
    public AuthResponse login(LoginRequest req, String requestId) {
        String email = req.email().trim();
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, req.password())
            );

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new AuthException("User not found"));

            String token = jwtUtil.generateToken(user.getId());

            return new AuthResponse(token);
        } catch (BadCredentialsException | UsernameNotFoundException ex) {
            meterRegistry.counter("auth.login.failed").increment();
            throw new AuthException("Invalid email or password.");
        } catch (Exception ex) {
            meterRegistry.counter("auth.login.failed").increment();
            throw new AuthException("Authentication failed. Please try again.");
        }
    }

    @Transactional
    @Timed(value = "auth.forgotPassword", description = "Time taken to handle forgot password request")
    public void forgotPassword(ForgotPassword req, String requestId) {
        Optional<User> user = userService.findByEmail(req.email());
        if (user.isEmpty()) {
            return;
        }

        User existingUser = user.get();

//        generate the reset token
        String resetKey = buildResetKey(existingUser.getId());
        Duration expiryDuration = Duration.ofMinutes(20);
        String resetToken = jwtUtil.generateResetToken(existingUser.getId(), expiryDuration);
        String base64Token = Base64.getEncoder().encodeToString(resetToken.getBytes());

        redisTemplate.opsForHash().put(resetKey, "token", base64Token);
        redisTemplate.opsForHash().put(resetKey, "verified", "false");
        redisTemplate.expire(resetKey, expiryDuration);

        eventPublisher.publishEvent(new PasswordResetTokenGeneratedEvent(existingUser.getFirstName(), existingUser.getEmail(), base64Token, expiryDuration, requestId));
    }

    public void verifyResetToken(VerifyResetToken req) {
        try {
            String resetToken = new String(Base64.getDecoder().decode(req.resetToken()));
            UUID userId = jwtUtil.extractUserId(resetToken);

            String key = buildResetKey(userId);

            Object storedToken = redisTemplate.opsForHash().get(key, "token");
            if (storedToken == null || !storedToken.toString().equals(req.resetToken())) {
                throw new AuthException("Invalid or expired reset token.");
            }

            redisTemplate.opsForHash().put(key, "verified", "true");
        } catch (IllegalArgumentException e) {
            throw new AuthException("Malformed reset token.");
        }
    }

    @Transactional
    public void resetPassword(ResetPassword req, String requestId) {
        try {
            String resetToken = new String(Base64.getDecoder().decode(req.resetToken()));
            UUID userId = jwtUtil.extractUserId(resetToken);

            Optional<User> user = userService.findById(userId);
            if (user.isEmpty()) {
                throw new AuthException("Invalid reset token.");
            }

            String key = buildResetKey(user.get().getId());

            Object tokenObj = redisTemplate.opsForHash().get(key, "token");
            Object verifiedObj = redisTemplate.opsForHash().get(key, "verified");

            if (tokenObj == null || !req.resetToken().equals(tokenObj.toString())) {
                throw new AuthException("Invalid or expired reset token.");
            }

            if (verifiedObj == null || !"true".equals(verifiedObj.toString())) {
                throw new AuthException("Reset token has not been verified.");
            }

            userService.updatePassword(user.get(), req.newPassword());

            redisTemplate.delete(key);

            eventPublisher.publishEvent(
                    new SuccessfulPasswordResetEvent(
                            requestId,
                            user.get().getEmail()
                    )
            );
        } catch (IllegalArgumentException e) {
            throw new AuthException("Malformed reset token.");
        }
    }

    private String buildResetKey(UUID userId) {
        return "password-reset:" + userId.toString();
    }

    @Timed(value = "auth.verifyEmail", description = "Time taken to verify user's email")
    public void verifyEmail(VerifyEmailRequest req, String requestId) {
        try {
            String decodedToken = new String(Base64.getDecoder().decode(req.token()));

            UUID userId = jwtUtil.extractUserId(decodedToken);

            if (!jwtUtil.isValid(decodedToken)) {
                throw new IllegalArgumentException("Invalid or expired verification token.");
            }

            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isEmpty()) {
                throw new IllegalArgumentException("Invalid verification token.");
            }

            User user = userOpt.get();
            userService.markEmailAsVerified(user);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid verification token.");
        }
    }

    @Timed(value = "auth.resendVerification", description = "Time taken to resend verification email")
    public void resendVerificationEmail(String requestId, UUID currentUserId) {
        // Get user by ID from JWT token
        Optional<User> userOpt = userService.findById(currentUserId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found.");
        }

        User user = userOpt.get();

        // Check if email is already verified
        if (user.getEmailVerifiedAt() != null) {
            throw new IllegalArgumentException("Email is already verified.");
        }

        // Send verification email
        sendVerificationEmail(user, requestId);
    }

    private void sendVerificationEmail(User user, String requestId) {
        int expirationMinutes = 20;
        String verificationToken = jwtUtil.generateEmailVerificationToken(user.getId(), expirationMinutes);
        String base64Token = Base64.getEncoder().encodeToString(verificationToken.getBytes());

        publishUserRegisteredEventAsync(
                user,
                base64Token,
                expirationMinutes,
                requestId
        );
    }
}
