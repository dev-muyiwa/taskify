package com.devmuyiwa.taskify.common.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class ApiErrorResponse {
    @Builder.Default
    Boolean success = false;
    String message;
    Object error;
    String requestId;
    String timestamp = Instant.now().toString();
    String path;
}
