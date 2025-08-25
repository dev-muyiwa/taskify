package com.devmuyiwa.taskify.auth.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(name = "RegisterRequestDto", description = "Request object for user registration")
public record RegisterRequest(
        @Schema(description = "First name of the user", example = "Moyosoreoluwa")
        @NotBlank(message = "First name is required")
        @Size(min = 2, message = "First name must be at least 2 characters long")
        @Pattern(
                regexp = "^\\p{L}+(?:['’-]\\p{L}+)*$",
                message = "First name must contain only letters, apostrophes, or hyphens"
        )
        String firstName,

        @Schema(description = "Last name of the user", example = "Odusola")
        @NotBlank(message = "Last name is required")
        @Size(min = 2, message = "Last name must be at least 2 characters long")
        @Pattern(
                regexp = "^\\p{L}+(?:['’-]\\p{L}+)*$",
                message = "Last name must contain only letters, apostrophes, or hyphens"
        )
        String lastName,

        @Schema(description = "Email address of the user", example = "muyiwatemilade@gmail.com")
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        String email,

        @Schema(description = "Password for the user account", example = "P@ssw0rd!-123")
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters long")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?\\-&])[A-Za-z\\d@$!%*?&-]{8,}$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
        )
        String password,

        @Schema(description = "Whether the user has accepted the terms and conditions", example = "true")
        @AssertTrue(message = "You must agree to the terms and conditions")
        boolean hasAcceptedTerms
) {
}
