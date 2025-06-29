package com.devmuyiwa.taskify.workspace.domain.external;

import com.devmuyiwa.taskify.user.domain.User;
import com.devmuyiwa.taskify.workspace.domain.internal.WorkspaceMemberRole;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "workspace_members", uniqueConstraints = {@UniqueConstraint(columnNames = {"workspace_id", "user_id"})})
public class WorkspaceMember {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "avatar_key")
    private String avatarKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private WorkspaceMemberRole role;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;


    //    Relationships
    @ManyToOne
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
