package com.devmuyiwa.taskify.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.Map;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
        name = "ApiSuccessResponse",
        description = "Enterprise-grade response format for successful API requests")
public class ApiSuccessResponse<T> {
    
    @Builder.Default
    @Schema(description = "Indicates whether the request was successful", example = "true")
    boolean success = true;
    
    @Schema(description = "HTTP status code", example = "200")
    Integer status;
    
    @Schema(description = "HTTP status text", example = "OK")
    String statusText;
    
    @Schema(description = "Message describing the result of the request", example = "Operation completed successfully.")
    String message;
    
    @Schema(description = "Data returned by the API, if any")
    T data;
    
    @Schema(description = "Pagination information for list responses")
    PaginationInfo pagination;
    
    @Schema(description = "Additional metadata about the response")
    Map<String, Object> metadata;
    
    @Schema(description = "Unique identifier for the request, useful for tracing and debugging", example = "req-123e4567-e89b-12d3-a456-426614174000")
    String requestId;
    
    @Schema(description = "Timestamp of when the response was generated", example = "2023-10-01T12:34:56.789Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    Instant timestamp;
    
    @Schema(description = "Path of the request", example = "/api/v1/users")
    String path;
    
    @Schema(description = "HTTP method of the request", example = "GET")
    String method;
    
    @Schema(description = "Application version", example = "1.0.0")
    String version;
    
    @Schema(description = "Instance ID of the service", example = "taskify-api-01")
    String instanceId;
    
    @Schema(description = "Processing time in milliseconds", example = "45")
    Long processingTimeMs;
    
    
    
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
        
        @Schema(description = "Sort information")
        SortInfo sort;
        
        @Schema(description = "Navigation links")
        NavigationLinks navigation;
    }
    
    @Value
    @Builder
    @Schema(description = "Sort information for paginated responses")
    public static class SortInfo {
        @Schema(description = "Whether the response is sorted", example = "true")
        Boolean sorted;
        
        @Schema(description = "Whether the response is unsorted", example = "false")
        Boolean unsorted;
        
        @Schema(description = "Sort direction", example = "ASC")
        String direction;
        
        @Schema(description = "Sort property", example = "createdAt")
        String property;
    }
    
    @Value
    @Builder
    @Schema(description = "Navigation links for pagination")
    public static class NavigationLinks {
        @Schema(description = "Link to first page")
        String first;
        
        @Schema(description = "Link to previous page")
        String prev;
        
        @Schema(description = "Link to self (current page)")
        String self;
        
        @Schema(description = "Link to next page")
        String next;
        
        @Schema(description = "Link to last page")
        String last;
    }
}
