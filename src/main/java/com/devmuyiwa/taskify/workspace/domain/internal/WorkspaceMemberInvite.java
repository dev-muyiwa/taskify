package com.devmuyiwa.taskify.workspace.domain.internal;

import com.devmuyiwa.taskify.user.domain.User;
import com.devmuyiwa.taskify.workspace.domain.external.Workspace;
import com.devmuyiwa.taskify.workspace.domain.external.WorkspaceMember;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "workspace_member_invites", uniqueConstraints = @UniqueConstraint(columnNames = {"workspace_id", "email"}))
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class WorkspaceMemberInvite {

    @Id @GeneratedValue
    private UUID id;

    @Column(name = "valid_till", nullable = false)
    private Instant validTill;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;


    // Relationships
    @ManyToOne
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "invited_by_id", nullable = false)
    private WorkspaceMember invitedBy;
}
