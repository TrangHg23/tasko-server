package com.hoangtrang.taskoserver.service;

import com.hoangtrang.taskoserver.dto.task.CreateTaskRequest;
import com.hoangtrang.taskoserver.dto.task.TaskResponse;

import java.util.UUID;

public interface TaskService {
    TaskResponse createTask(CreateTaskRequest request, UUID userId);
}
