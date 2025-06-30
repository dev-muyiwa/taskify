package com.devmuyiwa.taskify.common.events;

public record WorkspaceMemberCreatedEvent(
        String firstName,
        String email
) {
}
