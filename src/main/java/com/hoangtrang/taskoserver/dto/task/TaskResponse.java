package com.hoangtrang.taskoserver.dto.task;

import com.hoangtrang.taskoserver.dto.category.CategoryResponse;
import com.hoangtrang.taskoserver.model.enums.PriorityLevel;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        String title,
        String description,
        LocalDate dueDate,
        PriorityLevel priority,
        Boolean isCompleted,
        OffsetDateTime completedAt,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        CategoryResponse category,
        boolean isInboxTask,
        boolean isTodayTask,
        boolean isOverdue
){}
