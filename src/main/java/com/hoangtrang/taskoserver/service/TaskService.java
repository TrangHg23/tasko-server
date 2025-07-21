package com.hoangtrang.taskoserver.service;

import com.hoangtrang.taskoserver.dto.task.CountTaskResponse;
import com.hoangtrang.taskoserver.dto.task.TaskRequest;
import com.hoangtrang.taskoserver.dto.task.TaskResponse;
import com.hoangtrang.taskoserver.dto.task.UpdateTaskRequest;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    TaskResponse createTask(TaskRequest request, UUID userId);

    List<TaskResponse> filterTasks(UUID userId, String status, String due, UUID categoryId, Boolean inbox);

    CountTaskResponse countTasks(UUID userId);

    TaskResponse updateTask(UUID taskId, UUID userId, TaskRequest updateData);

    TaskResponse patchTask(UUID taskId, UUID userId, UpdateTaskRequest updateTask);

    void deleteTask(UUID taskId, UUID userId);
}
