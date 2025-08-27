package com.devmuyiwa.taskify.auth;

import com.devmuyiwa.taskify.auth.dto.req.*;
import com.devmuyiwa.taskify.auth.dto.res.AuthResponse;
import com.devmuyiwa.taskify.auth.util.AuthUser;
import com.devmuyiwa.taskify.common.dto.ApiResponseBuilder;
import com.devmuyiwa.taskify.common.dto.ApiSuccessResponse;
import com.devmuyiwa.taskify.common.filter.RequestIdFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication")
public class AuthController {

    private final AuthService authService;
    private final ApiResponseBuilder responseBuilder;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    @ApiResponse(useReturnTypeSchema = true, description = "Registers a new user and returns an authentication token.",
            responseCode = "201")
    public ApiSuccessResponse<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest) {
        String requestId = (String) httpRequest.getAttribute(RequestIdFilter.REQUEST_ID_HEADER);

        AuthResponse response = authService.register(request, requestId);
        return responseBuilder.success(response, "User registered successfully.");
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Login an existing user")
    @ApiResponse(useReturnTypeSchema = true, description = "Logs in a user and returns an authentication token.",
            responseCode = "200")
    public ApiSuccessResponse<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        String requestId = (String) httpRequest.getAttribute(RequestIdFilter.REQUEST_ID_HEADER);

        AuthResponse response = authService.login(request, requestId);
        return responseBuilder.success(response, "User logged in successfully.");
    }

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Request password reset")
    @ApiResponse(useReturnTypeSchema = true, description = "Sends a password reset link to the user's email.",
            responseCode = "200")
    public ApiSuccessResponse<Void> forgotPassword(
            @Valid @RequestBody ForgotPassword request,
            HttpServletRequest httpRequest) {
        String requestId = (String) httpRequest.getAttribute(RequestIdFilter.REQUEST_ID_HEADER);

        authService.forgotPassword(request, requestId);
        return responseBuilder.success("If an account with that email exists, a password reset link has been sent.");
    }

    @PostMapping("/verify-reset-token")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Verify password reset token")
    @ApiResponse(useReturnTypeSchema = true, description = "Verifies the password reset token.",
            responseCode = "200")
    public ApiSuccessResponse<Void> verifyResetToken(
            @Valid @RequestBody VerifyResetToken request,
            HttpServletRequest httpRequest) {
        String requestId = (String) httpRequest.getAttribute(RequestIdFilter.REQUEST_ID_HEADER);

        authService.verifyResetToken(request);
        return responseBuilder.success("Password reset token is valid.");
    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Reset user's password")
    @ApiResponse(useReturnTypeSchema = true, description = "Resets the user's password using the provided token.",
            responseCode = "200")
    public ApiSuccessResponse<Void> resetPassword(
            @Valid @RequestBody ResetPassword request,
            HttpServletRequest httpRequest) {
        String requestId = (String) httpRequest.getAttribute(RequestIdFilter.REQUEST_ID_HEADER);

        authService.resetPassword(request, requestId);
        return responseBuilder.success("Password reset successfully.");
    }

    @PatchMapping("/verify-email")
    @Operation(summary = "Verify user's email")
    @ApiResponse(useReturnTypeSchema = true, description = "Verifies the user's email using the provided token.",
            responseCode = "200")
    public ApiSuccessResponse<Void> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request,
            HttpServletRequest httpRequest) {
        String requestId = (String) httpRequest.getAttribute(RequestIdFilter.REQUEST_ID_HEADER);

        authService.verifyEmail(request, requestId);
        return responseBuilder.success("Email verified successfully.");
    }

    @PostMapping("/resend-verification")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Resend email verification")
    @ApiResponse(useReturnTypeSchema = true, description = "Resends verification email to the authenticated user.",
            responseCode = "200")
    @SecurityRequirement(name = "BearerAuth")
    public ApiSuccessResponse<Void> resendVerification(
            @AuthenticationPrincipal AuthUser authUser,
            HttpServletRequest httpRequest) {
        String requestId = (String) httpRequest.getAttribute(RequestIdFilter.REQUEST_ID_HEADER);

        authService.resendVerificationEmail(requestId, authUser.id());
        return responseBuilder.success("Email resend successfully.");
    }
}
