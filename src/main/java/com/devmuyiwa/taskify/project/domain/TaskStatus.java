package com.devmuyiwa.taskify.project.domain;

import com.devmuyiwa.taskify.workspace.domain.external.Workspace;
import com.devmuyiwa.taskify.workspace.domain.external.WorkspaceMember;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "task_statuses", uniqueConstraints = @UniqueConstraint(columnNames = {"workspace_id", "name"}))
public class TaskStatus {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

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
    @JoinColumn(name = "author_id", nullable = false)
    private WorkspaceMember author;
}
