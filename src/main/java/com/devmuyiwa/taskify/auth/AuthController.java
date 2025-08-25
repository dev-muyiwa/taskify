package com.devmuyiwa.taskify.auth;

import com.devmuyiwa.taskify.auth.dto.req.*;
import com.devmuyiwa.taskify.auth.dto.res.AuthResponse;
import com.devmuyiwa.taskify.common.dto.ApiSuccessResponse;
import com.devmuyiwa.taskify.common.filter.RequestIdFilter;
import com.devmuyiwa.taskify.auth.util.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    @ApiResponse(useReturnTypeSchema = true, description = "Registers a new user and returns an authentication token.",
            responseCode = "201")
    public ResponseEntity<ApiSuccessResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest) {
        String requestId = (String) httpRequest.getAttribute(RequestIdFilter.REQUEST_ID_HEADER);

        AuthResponse response = authService.register(request, requestId);
        return ResponseEntity.ok(
                ApiSuccessResponse
                        .<AuthResponse>builder()
                        .message("User registered successfully.")
                        .data(response)
                        .requestId(requestId)
                        .build()
        );
    }

    @PostMapping("/login")
    @Operation(summary = "Login an existing user")
    @ApiResponse(useReturnTypeSchema = true, description = "Logs in a user and returns an authentication token.",
            responseCode = "200")
    public ResponseEntity<ApiSuccessResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        String requestId = (String) httpRequest.getAttribute(RequestIdFilter.REQUEST_ID_HEADER);

        AuthResponse response = authService.login(request, requestId);
        return ResponseEntity.ok(
                ApiSuccessResponse
                        .<AuthResponse>builder()
                        .message("User logged in successfully.")
                        .data(response)
                        .requestId(requestId)
                        .build()
        );
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset")
    @ApiResponse(useReturnTypeSchema = true, description = "Sends a password reset link to the user's email.",
            responseCode = "200")
    public ResponseEntity<ApiSuccessResponse<String>> forgotPassword(
            @Valid @RequestBody ForgotPassword request,
            HttpServletRequest httpRequest) {
        String requestId = (String) httpRequest.getAttribute(RequestIdFilter.REQUEST_ID_HEADER);

        authService.forgotPassword(request, requestId);
        return ResponseEntity.ok(
                ApiSuccessResponse
                        .<String>builder()
                        .message("If an account with that email exists, a password reset link has been sent.")
                        .data(null)
                        .requestId(requestId)
                        .build()
        );
    }

    @PostMapping("/verify-reset-token")
    @Operation(summary = "Verify password reset token")
    @ApiResponse(useReturnTypeSchema = true, description = "Verifies the password reset token.",
            responseCode = "200")
    public ResponseEntity<ApiSuccessResponse<String>> verifyResetToken(
            @Valid @RequestBody VerifyResetToken request,
            HttpServletRequest httpRequest) {
        String requestId = (String) httpRequest.getAttribute(RequestIdFilter.REQUEST_ID_HEADER);

        authService.verifyResetToken(request);
        return ResponseEntity.ok(
                ApiSuccessResponse
                        .<String>builder()
                        .message("Password reset token verified successfully.")
                        .data(null)
                        .requestId(requestId)
                        .build()
        );
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset user's password")
    @ApiResponse(useReturnTypeSchema = true, description = "Resets the user's password using the provided token.",
            responseCode = "200")
    public ResponseEntity<ApiSuccessResponse<String>> resetPassword(
            @Valid @RequestBody ResetPassword request,
            HttpServletRequest httpRequest) {
        String requestId = (String) httpRequest.getAttribute(RequestIdFilter.REQUEST_ID_HEADER);

        authService.resetPassword(request, requestId);
        return ResponseEntity.ok(
                ApiSuccessResponse
                        .<String>builder()
                        .message("Password reset successfully.")
                        .data(null)
                        .requestId(requestId)
                        .build()
        );
    }

    @PatchMapping("/verify-email")
    @Operation(summary = "Verify user's email")
    @ApiResponse(useReturnTypeSchema = true, description = "Verifies the user's email using the provided token.",
            responseCode = "200")
    public ResponseEntity<ApiSuccessResponse<String>> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request,
            HttpServletRequest httpRequest) {
        String requestId = (String) httpRequest.getAttribute(RequestIdFilter.REQUEST_ID_HEADER);

        authService.verifyEmail(request, requestId);
        return ResponseEntity.ok(
                ApiSuccessResponse
                        .<String>builder()
                        .message("Email verified successfully.")
                        .data(null)
                        .requestId(requestId)
                        .build()
        );
    }

    @PostMapping("/resend-verification")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Resend email verification")
    @ApiResponse(useReturnTypeSchema = true, description = "Resends verification email to the authenticated user.",
            responseCode = "200")
    public ResponseEntity<ApiSuccessResponse<String>> resendVerification(
            @AuthenticationPrincipal AuthUser authUser,
            HttpServletRequest httpRequest) {
        String requestId = (String) httpRequest.getAttribute(RequestIdFilter.REQUEST_ID_HEADER);

        if (authUser == null) {
            throw new UnsupportedOperationException("User not authenticated");
        }

        authService.resendVerificationEmail(requestId, authUser.id());
        return ResponseEntity.ok(
                ApiSuccessResponse
                        .<String>builder()
                        .message("Verification email has been resent.")
                        .data(null)
                        .requestId(requestId)
                        .build()
        );
    }
}
