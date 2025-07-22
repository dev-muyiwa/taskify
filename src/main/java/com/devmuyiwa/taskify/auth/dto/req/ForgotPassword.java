package com.devmuyiwa.taskify.auth.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "ForgotPasswordRequestDto", description = "Request object for password reset")
public record ForgotPassword(
        @Schema(description = "Email address of the user requesting password reset", example = "john@doe.com")
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        String email
) {
}
