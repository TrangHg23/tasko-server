package com.hoangtrang.taskoserver.model;

import com.hoangtrang.taskoserver.model.enums.DueType;
import com.hoangtrang.taskoserver.model.enums.PriorityLevel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name="tasks")
public class Task {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name="user_id", nullable = false)
    private UUID userId;

    @Column(name="category_id")
    private UUID categoryId;

    private String title;
    private String description;

    @Column(name="due_at_utc")
    private OffsetDateTime dueAtUtc;

    @Enumerated(EnumType.STRING)
    @Column(name="due_type")
    private DueType dueType;

    @Enumerated(EnumType.STRING)
    private PriorityLevel priority;

    @Column(name="is_completed")
    private Boolean isCompleted;

    @Column(name="completed_at")
    private OffsetDateTime completedAt;

    @Column(name="created_at")
    private OffsetDateTime createdAt;

    @Column(name="updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
        if (isCompleted == null) {
            isCompleted = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public boolean isInboxTask() {
        return this.categoryId == null;
    }

    public boolean hasDueDate() {
        return dueType != null && dueType != DueType.NONE;
    }

    public boolean isTodayTask() {
        if (!hasDueDate()) return false;
        LocalDate taskDate = dueAtUtc.toLocalDate();
        return taskDate.equals(LocalDate.now());
    }

    public boolean isOverdue() {
        if (isCompleted || !hasDueDate()) return false;
        return dueAtUtc.isBefore(OffsetDateTime.now());
    }

    public LocalDate getDueDate() {
        return hasDueDate() ? dueAtUtc.toLocalDate() : null;
    }

    public OffsetDateTime getDueDateTime() {
        return (dueType == DueType.DATE_TIME) ? dueAtUtc : null;
    }
}
