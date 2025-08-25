package com.devmuyiwa.taskify.common.dto;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Utility class for building enterprise-grade API responses
 */
@Component
public class ApiResponseBuilder {

    @Value("${spring.application.version:1.0.0}")
    private String applicationVersion;

    @Value("${server.instance.id:${random.uuid}}")
    private String instanceId;

    /**
     * Build a success response with data
     */
    public <T> ApiSuccessResponse<T> success(T data, String message) {
        return ApiSuccessResponse.<T>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .statusText(HttpStatus.OK.getReasonPhrase())
                .message(message)
                .data(data)
                .requestId(getRequestId())
                .timestamp(Instant.now())
                .path(getRequestPath())
                .method(getRequestMethod())
                .version(applicationVersion)
                .instanceId(instanceId)
                .build();
    }

    /**
     * Build a success response without data
     */
    public ApiSuccessResponse<Void> success(String message) {
        return ApiSuccessResponse.<Void>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .statusText(HttpStatus.OK.getReasonPhrase())
                .message(message)
                .requestId(getRequestId())
                .timestamp(Instant.now())
                .path(getRequestPath())
                .method(getRequestMethod())
                .version(applicationVersion)
                .instanceId(instanceId)
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
                .status(HttpStatus.OK.value())
                .statusText(HttpStatus.OK.getReasonPhrase())
                .message(message)
                .data(data)
                .pagination(pagination)
                .requestId(getRequestId())
                .timestamp(Instant.now())
                .path(getRequestPath())
                .method(getRequestMethod())
                .version(applicationVersion)
                .instanceId(instanceId)
                .build();
    }

    /**
     * Build an error response
     */
    public ApiErrorResponse error(
            HttpStatus status,
            String errorCode,
            String message,
            String detail,
            String errorType,
            Object error,
            List<ApiErrorResponse.FieldError> fieldErrors,
            Map<String, Object> metadata
    ) {
        return ApiErrorResponse.builder()
                .success(false)
                .status(status.value())
                .statusText(status.getReasonPhrase())
                .errorCode(errorCode)
                .message(message)
                .detail(detail)
                .errorType(errorType)
                .error(error)
                .fieldErrors(fieldErrors)
                .metadata(metadata)
                .requestId(getRequestId())
                .timestamp(Instant.now())
                .path(getRequestPath())
                .method(getRequestMethod())
                .userAgent(getUserAgent())
                .clientIp(getClientIp())
                .version(applicationVersion)
                .instanceId(instanceId)
                .retryAfter(getRetryAfter(status))
                .build();
    }

    /**
     * Build a validation error response
     */
    public ApiErrorResponse validationError(
            String message,
            List<ApiErrorResponse.FieldError> fieldErrors
    ) {
        return error(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_FAILED",
                message,
                "One or more fields failed validation",
                "VALIDATION_ERROR",
                null,
                fieldErrors,
                Map.of("validationType", "FIELD_VALIDATION")
        );
    }

    /**
     * Build an authentication error response
     */
    public ApiErrorResponse authenticationError(
            String errorCode,
            String message,
            String detail
    ) {
        return error(
                HttpStatus.UNAUTHORIZED,
                errorCode,
                message,
                detail,
                "AUTHENTICATION_ERROR",
                null,
                null,
                Map.of("authType", "JWT", "requiresAuth", true)
        );
    }

    /**
     * Build an authorization error response
     */
    public ApiErrorResponse authorizationError(
            String errorCode,
            String message,
            String detail
    ) {
        return error(
                HttpStatus.FORBIDDEN,
                errorCode,
                message,
                detail,
                "AUTHORIZATION_ERROR",
                null,
                null,
                Map.of("requiresPermission", true)
        );
    }

    /**
     * Build a not found error response
     */
    public ApiErrorResponse notFoundError(
            String errorCode,
            String message,
            String detail
    ) {
        return error(
                HttpStatus.NOT_FOUND,
                errorCode,
                message,
                detail,
                "NOT_FOUND_ERROR",
                null,
                null,
                Map.of("resourceType", "ENTITY")
        );
    }

    /**
     * Build a server error response
     */
    public ApiErrorResponse serverError(
            String errorCode,
            String message,
            String detail
    ) {
        return error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                errorCode,
                message,
                detail,
                "SERVER_ERROR",
                null,
                null,
                Map.of("errorCategory", "SYSTEM_ERROR")
        );
    }

    // Helper methods
    private String getRequestId() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return (String) request.getAttribute("requestId");
            }
        } catch (Exception e) {
            // Ignore if request context is not available
        }
        return "N/A";
    }

    private String getRequestPath() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getRequestURI();
            }
        } catch (Exception e) {
            // Ignore if request context is not available
        }
        return "N/A";
    }

    private String getRequestMethod() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getMethod();
            }
        } catch (Exception e) {
            // Ignore if request context is not available
        }
        return "N/A";
    }

    private String getUserAgent() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getHeader("User-Agent");
            }
        } catch (Exception e) {
            // Ignore if request context is not available
        }
        return "N/A";
    }

    private String getClientIp() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    return xForwardedFor.split(",")[0].trim();
                }
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            // Ignore if request context is not available
        }
        return "N/A";
    }

    private String buildHelpUrl(String errorCode) { return null; }

    private boolean isRetryable(HttpStatus status) { return false; }

    private Long getRetryAfter(HttpStatus status) {
        if (status == HttpStatus.TOO_MANY_REQUESTS) {
            return 60000L; // 1 minute
        } else if (status == HttpStatus.SERVICE_UNAVAILABLE) {
            return 30000L; // 30 seconds
        } else if (status == HttpStatus.GATEWAY_TIMEOUT) {
            return 15000L; // 15 seconds
        }
        return null;
    }
}
