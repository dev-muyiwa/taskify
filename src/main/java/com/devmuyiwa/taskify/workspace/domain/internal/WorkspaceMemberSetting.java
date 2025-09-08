package com.devmuyiwa.taskify.workspace.domain.internal;

import com.devmuyiwa.taskify.workspace.domain.external.WorkspaceMember;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "workspace_member_settings")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class WorkspaceMemberSetting {

    @Id
    @GeneratedValue
    private Long id;

    @Builder.Default
    @Column(name = "should_send_task_assigned_email", nullable = false)
    private boolean shouldSendTaskAssignedEmail = true;

    @Builder.Default
    @Column(name = "should_send_task_completed_email", nullable = false)
    private boolean shouldSendTaskCompletedEmail = true;

    @Builder.Default
    @Column(name = "should_send_task_due_email", nullable = false)
    private boolean shouldSendProjectUpdatedEmail = false;

    @Builder.Default
    @Column(name = "should_send_team_invites_email", nullable = false)
    private boolean shouldSendTeamInvitesEmail = false;

    @Builder.Default
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

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        WorkspaceMemberSetting that = (WorkspaceMemberSetting) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
