package com.hoangtrang.taskoserver.dto.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hoangtrang.taskoserver.model.enums.PriorityLevel;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.UUID;

public record TaskRequest(
        @NotBlank(message = "TITLE_REQUIRED")
        String title,

        String description,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate dueDate,

        PriorityLevel priority,

        UUID categoryId
) {}
