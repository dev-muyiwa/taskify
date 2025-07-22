package com.devmuyiwa.taskify.common.events;

import java.time.Duration;

public record PasswordResetTokenGeneratedEvent(
        String firstName,
        String email,
        String token,
        Duration expirationTime,
        String requestId) {
}
