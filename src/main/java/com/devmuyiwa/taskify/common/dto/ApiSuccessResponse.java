package com.devmuyiwa.taskify.common.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ApiSuccessResponse<T> {
    @Builder.Default
    boolean success = true;
    String message;
    @Builder.Default
    T data = null;
    String requestId;
}
