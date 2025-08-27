package com.devmuyiwa.taskify.common.dto;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.List;

/**
 * Utility class for building simplified API responses
 */
@Component
public class ApiResponseBuilder {

    /**
     * Build a success response with data
     */
    public <T> ApiSuccessResponse<T> success(T data, String message) {
        return ApiSuccessResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .path(getRequestPath())
                .method(getRequestMethod())
                .build();
    }

    /**
     * Build a success response without data
     */
    public ApiSuccessResponse<Void> success(String message) {
        return ApiSuccessResponse.<Void>builder()
                .success(true)
                .message(message)
                .timestamp(Instant.now())
                .path(getRequestPath())
                .method(getRequestMethod())
                .build();
    }

    /**
     * Build a paginated success response
     */
    public <T> ApiSuccessResponse<List<T>> successPaginated(
            List<T> data,
            String message,
            int page,
            int size,
            long totalElements,
            int totalPages,
            boolean first,
            boolean last,
            int numberOfElements
    ) {
        ApiSuccessResponse.PaginationInfo pagination = ApiSuccessResponse.PaginationInfo.builder()
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(first)
                .last(last)
                .numberOfElements(numberOfElements)
                .build();

        return ApiSuccessResponse.<List<T>>builder()
                .success(true)
                .message(message)
                .data(data)
                .pagination(pagination)
                .timestamp(Instant.now())
                .path(getRequestPath())
                .method(getRequestMethod())
                .build();
    }

    /**
     * Build an error response
     */
    public ApiErrorResponse error(String message, Object data, List<ApiErrorResponse.FieldError> fieldErrors) {
        return ApiErrorResponse.builder()
                .success(false)
                .message(message)
                .data(data)
                .fieldErrors(fieldErrors)
                .timestamp(Instant.now())
                .path(getRequestPath())
                .method(getRequestMethod())
                .build();
    }

    /**
     * Build a validation error response
     */
    public ApiErrorResponse validationError(String message, List<ApiErrorResponse.FieldError> fieldErrors) {
        return error(message, null, fieldErrors);
    }

    /**
     * Build an authentication error response
     */
    public ApiErrorResponse authenticationError(String message) {
        return error(message, null, null);
    }

    /**
     * Build an authorization error response
     */
    public ApiErrorResponse authorizationError(String message) {
        return error(message, null, null);
    }

    /**
     * Build a not found error response
     */
    public ApiErrorResponse notFoundError(String message) {
        return error(message, null, null);
    }

    /**
     * Build a server error response
     */
    public ApiErrorResponse serverError(String message) {
        return error(message, null, null);
    }

    // Helper methods
    private String getRequestPath() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest().getRequestURI() : "/";
        } catch (Exception e) {
            return "/";
        }
    }

    private String getRequestMethod() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest().getMethod() : "UNKNOWN";
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }
}
