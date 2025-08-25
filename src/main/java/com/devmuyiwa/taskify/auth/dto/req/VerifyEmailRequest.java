package com.devmuyiwa.taskify.auth.dto.req;

import jakarta.validation.constraints.NotBlank;

public record VerifyEmailRequest(
    @NotBlank(message = "Verification token is required")
    String token
) {
}
