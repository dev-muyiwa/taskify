package com.devmuyiwa.taskify.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
        name = "ApiErrorResponse",
        description = "Simplified error response format for API errors")
public class ApiErrorResponse {
    
    @Builder.Default
    @Schema(description = "Indicates whether the request was successful", example = "false")
    Boolean success = false;
    
    @Schema(description = "Human-readable error message", example = "Validation failed for the provided data")
    String message;
    
    @Schema(description = "Data returned by the API, if any")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Object data;
    
    @Schema(description = "Pagination information for list responses")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    PaginationInfo pagination;
    
    @Schema(description = "Timestamp of when the error occurred", example = "2023-10-01T12:34:56.789Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    Instant timestamp;
    
    @Schema(description = "Path of the request", example = "/api/v1/users")
    String path;
    
    @Schema(description = "HTTP method of the request", example = "POST")
    String method;
    
    @Schema(description = "List of field-specific validation errors")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<FieldError> fieldErrors;
    
    @Value
    @Builder
    @Schema(description = "Pagination information for list responses")
    public static class PaginationInfo {
        @Schema(description = "Current page number", example = "1")
        Integer page;
        
        @Schema(description = "Number of items per page", example = "20")
        Integer size;
        
        @Schema(description = "Total number of items", example = "150")
        Long totalElements;
        
        @Schema(description = "Total number of pages", example = "8")
        Integer totalPages;
        
        @Schema(description = "Whether this is the first page", example = "true")
        Boolean first;
        
        @Schema(description = "Whether this is the last page", example = "false")
        Boolean last;
        
        @Schema(description = "Number of items on current page", example = "20")
        Integer numberOfElements;
    }
    
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
    }
}
