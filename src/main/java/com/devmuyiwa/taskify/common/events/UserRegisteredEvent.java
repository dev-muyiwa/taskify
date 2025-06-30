package com.devmuyiwa.taskify.common.events;

import com.devmuyiwa.taskify.user.domain.User;

public record UserRegisteredEvent(String firstName, String lastName, User user) {
}
