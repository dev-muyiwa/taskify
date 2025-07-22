package com.devmuyiwa.taskify.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
@Schema(
        name = "ApiErrorResponse",
        description = "Standard response format for API errors")
public class ApiErrorResponse {
    @Builder.Default
    @Schema(description = "Indicates whether the request was successful", example = "false")
    Boolean success = false;
    @Schema(description = "Error code representing the type of error", example = "USER_NOT_FOUND")
    String message;
    @Schema(description = "Detailed error object containing specific error information")
    Object error;
    @Schema(description = "Unique identifier for the request, useful for tracing and debugging", example = "123e4567-e89b-12d3-a456-426614174000")
    String requestId;
    @Schema(description = "Timestamp of when the error occurred", example = "2023-10-01T12:34:56Z")
    String timestamp = Instant.now().toString();
    @Schema(description = "Path of the request that caused the error", example = "/api/v1/resource")
    String path;
}
