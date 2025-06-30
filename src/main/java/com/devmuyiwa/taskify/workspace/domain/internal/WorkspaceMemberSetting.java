package com.devmuyiwa.taskify.workspace.domain.internal;

import com.devmuyiwa.taskify.workspace.domain.external.WorkspaceMember;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "workspace_member_settings")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class WorkspaceMemberSetting {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "should_send_task_assigned_email", nullable = false)
    private boolean shouldSendTaskAssignedEmail = true;

    @Column(name = "should_send_task_completed_email", nullable = false)
    private boolean shouldSendTaskCompletedEmail = true;

    @Column(name = "should_send_task_due_email", nullable = false)
    private boolean shouldSendProjectUpdatedEmail = false;

    @Column(name = "should_send_team_invites_email", nullable = false)
    private boolean shouldSendTeamInvitesEmail = false;

    @Column(name = "should_send_task_due_pn", nullable = false)
    private boolean shouldSendTaskDuePn = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

//    Relationships
    @OneToOne
    @JoinColumn(name = "workspace_member_id", nullable = false, unique = true)
    private WorkspaceMember workspaceMember;
}
