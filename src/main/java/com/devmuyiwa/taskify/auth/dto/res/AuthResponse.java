package com.devmuyiwa.taskify.auth.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AuthResponseDto", description = "Response object for authentication operations")
public record AuthResponse(
        @Schema(description = "Access token for the authenticated user", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token
) {
}
