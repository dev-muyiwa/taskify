package com.devmuyiwa.taskify.auth.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema( name = "ResetPasswordRequestDto", description = "Request object for resetting user password")
public record ResetPassword(
        @Schema(description = "Reset token for password reset", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        @NotBlank(message = "Reset token is required")
        String resetToken,

        @Schema(description = "New Password for the user account", example = "P@ssw0rd!-123")
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters long")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?\\-&])[A-Za-z\\d@$!%*?&-]{8,}$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
        )
        String newPassword
) {
}
