package com.devmuyiwa.taskify.user.dto;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String firstName,
    String lastName,
    String email,
    Instant emailVerifiedAt,
    Instant createdAt
) {}
