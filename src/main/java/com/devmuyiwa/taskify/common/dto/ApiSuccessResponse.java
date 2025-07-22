package com.devmuyiwa.taskify.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@Schema(
        name = "ApiSuccessResponse",
        description = "Standard response format for successful API requests")
public class ApiSuccessResponse<T> {
    @Builder.Default
    @Schema(description = "Indicates whether the request was successful", example = "true")
    boolean success = true;
    @Schema(description = "Message describing the result of the request", example = "Operation completed successfully.")
    String message;
    @Builder.Default
    @Schema(description = "Data returned by the API, if any" ,example = "{\"key\": \"value\"}")
    T data = null;
    @Schema(description = "Unique identifier for the request, useful for tracing and debugging", example = "123e4567-e89b-12d3-a456-426614174000")
    String requestId;
}
