package com.hoangtrang.taskoserver.service;

import com.hoangtrang.taskoserver.dto.task.CountTaskResponse;
import com.hoangtrang.taskoserver.dto.task.CreateTaskRequest;
import com.hoangtrang.taskoserver.dto.task.TaskResponse;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    TaskResponse createTask(CreateTaskRequest request, UUID userId);

    List<TaskResponse> filterTasks(UUID userId, String status, String due, UUID categoryId, Boolean inbox);

    CountTaskResponse countTasks(UUID userId);

}
