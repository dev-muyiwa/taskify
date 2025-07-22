package com.devmuyiwa.taskify.auth.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "VerifyResetTokenRequestDto", description = "Request object for verifying password reset token")
public record VerifyResetToken(
        @Schema(description = "Email address of the user", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        @NotBlank(message = "Reset token is required")
        String resetToken
) {
}
