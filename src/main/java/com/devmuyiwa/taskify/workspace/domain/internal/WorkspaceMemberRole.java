package com.devmuyiwa.taskify.workspace.domain.internal;

public enum WorkspaceMemberRole {
    MEMBER,
    ADMIN,
    OWNER;

    public static WorkspaceMemberRole fromString(String role) {
        try {
            return WorkspaceMemberRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
    }
}
