package com.devmuyiwa.taskify.project.domain;

import com.devmuyiwa.taskify.workspace.domain.external.WorkspaceMember;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tasks", indexes = {
        @Index(name = "idx_task_project_id", columnList = "project_id"),
        @Index(name = "idx_task_status_id", columnList = "status_id"),
        @Index(name = "idx_task_assignee_id", columnList = "assignee_id"),
})
public class Task {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private TaskPriority priority;

    @Column(name = "story_points")
    private Integer storyPoints;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;


    // Relationships
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private TaskStatus status;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private WorkspaceMember assignee;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private WorkspaceMember author;

    @ManyToOne
    @JoinColumn(name = "parent_task_id")
    private Task parentTask;
}
