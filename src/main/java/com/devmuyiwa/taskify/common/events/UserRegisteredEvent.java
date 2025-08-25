package com.devmuyiwa.taskify.common.events;

public record UserRegisteredEvent(String firstName, String lastName, String email, String verificationToken,
                                  int expirationMinutes, String requestId) {
}
