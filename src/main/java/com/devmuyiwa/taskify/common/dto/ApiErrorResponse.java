package com.devmuyiwa.taskify.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
        name = "ApiErrorResponse",
        description = "Enterprise-grade error response format for API errors")
public class ApiErrorResponse {
    
    @Builder.Default
    @Schema(description = "Indicates whether the request was successful", example = "false")
    Boolean success = false;
    
    @Schema(description = "HTTP status code", example = "400")
    Integer status;
    
    @Schema(description = "HTTP status text", example = "Bad Request")
    String statusText;
    
    @Schema(description = "Application-specific error code", example = "VALIDATION_FAILED")
    String errorCode;
    
    @Schema(description = "Human-readable error message", example = "Validation failed for the provided data")
    String message;
    
    @Schema(description = "Detailed error description for developers", example = "One or more fields failed validation")
    String detail;
    
    @Schema(description = "Error category/type", example = "VALIDATION_ERROR")
    String errorType;
    
    @Schema(description = "Detailed error object containing specific error information")
    Object error;
    
    @Schema(description = "List of field-specific validation errors")
    List<FieldError> fieldErrors;
    
    @Schema(description = "Additional error context/metadata")
    Map<String, Object> metadata;
    
    @Schema(description = "Unique identifier for the request, useful for tracing and debugging", example = "req-123e4567-e89b-12d3-a456-426614174000")
    String requestId;
    
    @Schema(description = "Timestamp of when the error occurred", example = "2023-10-01T12:34:56.789Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    Instant timestamp;
    
    @Schema(description = "Path of the request that caused the error", example = "/api/v1/users")
    String path;
    
    @Schema(description = "HTTP method of the request", example = "POST")
    String method;
    
    @Schema(description = "User agent of the client", example = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
    String userAgent;
    
    @Schema(description = "Client IP address", example = "192.168.1.100")
    String clientIp;
    
    @Schema(description = "Application version", example = "1.0.0")
    String version;
    
    @Schema(description = "Instance ID of the service", example = "taskify-api-01")
    String instanceId;
    
    @Schema(description = "Suggested retry delay in milliseconds", example = "5000")
    Long retryAfter;
    
    @Value
    @Builder
    @Schema(description = "Field-specific validation error")
    public static class FieldError {
        @Schema(description = "Name of the field that failed validation", example = "email")
        String field;
        
        @Schema(description = "Value that failed validation", example = "invalid-email")
        Object rejectedValue;
        
        @Schema(description = "Error message for the field", example = "must be a valid email address")
        String message;
        
        @Schema(description = "Error code for the field", example = "EMAIL_FORMAT")
        String code;
        
        @Schema(description = "Additional context for the field error")
        Map<String, Object> context;
    }
    
    @Value
    @Builder
    @Schema(description = "Error metadata for additional context")
    public static class ErrorMetadata {
        @Schema(description = "Error severity level", example = "ERROR")
        String severity;
        
        @Schema(description = "Error category", example = "AUTHENTICATION")
        String category;
        
        @Schema(description = "Error subcategory", example = "JWT_EXPIRED")
        String subcategory;
        
        @Schema(description = "Error source", example = "JWT_FILTER")
        String source;
        
        @Schema(description = "Error context", example = "User authentication flow")
        String context;
        
        @Schema(description = "Additional error details")
        Map<String, Object> details;
    }
}
