package com.hoangtrang.taskoserver.service.impl;

import com.hoangtrang.taskoserver.dto.task.CreateTaskRequest;
import com.hoangtrang.taskoserver.dto.task.TaskResponse;
import com.hoangtrang.taskoserver.exception.AppException;
import com.hoangtrang.taskoserver.exception.ErrorStatus;
import com.hoangtrang.taskoserver.mapper.TaskMapper;
import com.hoangtrang.taskoserver.model.Category;
import com.hoangtrang.taskoserver.model.Task;
import com.hoangtrang.taskoserver.repository.CategoryRepository;
import com.hoangtrang.taskoserver.repository.TaskRepository;
import com.hoangtrang.taskoserver.service.TaskService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskServiceImpl implements TaskService {
    TaskRepository taskRepository;
    CategoryRepository categoryRepository;
    TaskMapper taskMapper;

    @Override
    public TaskResponse createTask(CreateTaskRequest request, UUID userId) {
        Task task = taskMapper.toTask(request, userId);

        Category category = null;
        if(request.categoryId() != null) {
            category = categoryRepository.findByIdAndUserId(request.categoryId(), userId)
                    .orElseThrow(() -> new AppException(ErrorStatus.CATEGORY_NOT_FOUND));
        }

        Task savedTask = taskRepository.save(task);
        return taskMapper.toTaskResponse(savedTask, category);
    }

}
