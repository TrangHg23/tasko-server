package com.hoangtrang.taskoserver.dto.task;

import com.hoangtrang.taskoserver.dto.category.CategoryResponse;
import com.hoangtrang.taskoserver.model.enums.DueType;
import com.hoangtrang.taskoserver.model.enums.PriorityLevel;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskResponse {
    UUID id;
    String title;
    String description;
    DueType dueType;
    LocalDate dueDate;
    OffsetDateTime dueDateTime;
    PriorityLevel priority;
    Boolean isCompleted;
    OffsetDateTime completedAt;
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;
    CategoryResponse category;
    boolean inboxTask;
    boolean todayTask;
    boolean overdue;
}

