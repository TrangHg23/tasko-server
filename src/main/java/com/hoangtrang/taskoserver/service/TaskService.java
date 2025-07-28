package com.hoangtrang.taskoserver.service;

import com.hoangtrang.taskoserver.dto.task.CountTaskResponse;
import com.hoangtrang.taskoserver.dto.task.TaskRequest;
import com.hoangtrang.taskoserver.dto.task.TaskResponse;
import com.hoangtrang.taskoserver.dto.task.UpdateTaskRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TaskService {
    TaskResponse createTask(TaskRequest request, UUID userId);

    List<TaskResponse> filterTasks(UUID userId, String status, LocalDate dueDate, UUID categoryId, Boolean inbox);

    Map<LocalDate, List<TaskResponse>> getTasksByDueDateList(UUID userId, List<LocalDate> dueDates);

    CountTaskResponse countTasks(UUID userId);

    TaskResponse updateTask(UUID taskId, UUID userId, TaskRequest updateData);

    TaskResponse patchTask(UUID taskId, UUID userId, UpdateTaskRequest updateTask);

    void deleteTask(UUID taskId, UUID userId);
}
