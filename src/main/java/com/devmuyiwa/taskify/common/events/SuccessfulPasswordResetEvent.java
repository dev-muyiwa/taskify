package com.devmuyiwa.taskify.common.events;

public record SuccessfulPasswordResetEvent(String requestId, String email) {
}
