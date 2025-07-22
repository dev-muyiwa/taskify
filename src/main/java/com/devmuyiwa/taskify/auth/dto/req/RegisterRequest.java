package com.devmuyiwa.taskify.auth.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "RegisterRequestDto", description = "Request object for user registration")
public record RegisterRequest(
        @Schema(description = "First name of the user", example = "John")
        @NotBlank(message = "First name is required")
        String firstName,

        @Schema(description = "Last name of the user", example = "Doe")
        @NotBlank(message = "Last name is required")
        String lastName,

        @Schema(description = "Email address of the user", example = "john@doe.com")
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        String email,

        @Schema(description = "Password for the user account", example = "password123")
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters long")
        String password,

        @Schema(description = "Whether the user has accepted the terms and conditions", example = "true")
        @AssertTrue(message = "You must agree to the terms and conditions")
        boolean termsAccepted
) {
}
