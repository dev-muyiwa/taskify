package com.devmuyiwa.taskify.auth.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema( name = "ResetPasswordRequestDto", description = "Request object for resetting user password")
public record ResetPassword(
        @Schema(description = "Reset token for password reset", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        @NotBlank(message = "Reset token is required")
        String resetToken,

        @Schema(description = "New password for the user account", example = "newPassword123")
        @NotBlank(message = "New password is required")
        @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters long")
        String newPassword
) {
}
