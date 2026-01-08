package com.hoangtrang.taskoserver.service.impl;

import com.hoangtrang.taskoserver.dto.task.CountTaskResponse;
import com.hoangtrang.taskoserver.dto.task.TaskRequest;
import com.hoangtrang.taskoserver.dto.task.TaskResponse;
import com.hoangtrang.taskoserver.dto.task.UpdateTaskRequest;
import com.hoangtrang.taskoserver.exception.AppException;
import com.hoangtrang.taskoserver.exception.ErrorStatus;
import com.hoangtrang.taskoserver.mapper.CategoryMapper;
import com.hoangtrang.taskoserver.mapper.TaskMapper;
import com.hoangtrang.taskoserver.model.Category;
import com.hoangtrang.taskoserver.model.Task;
import com.hoangtrang.taskoserver.model.enums.DueType;
import com.hoangtrang.taskoserver.repository.CategoryRepository;
import com.hoangtrang.taskoserver.repository.TaskRepository;
import com.hoangtrang.taskoserver.service.TaskDueTimeHelper;
import com.hoangtrang.taskoserver.service.TaskService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskServiceImpl implements TaskService {
    TaskRepository taskRepository;
    CategoryRepository categoryRepository;
    TaskMapper taskMapper;
    CategoryMapper categoryMapper;

    @Override
    @Transactional
    public TaskResponse createTask(TaskRequest request, UUID userId) {
        validateDueTimeRequest(request);

        Task task = taskMapper.toTask(request, userId);

        TaskDueTimeHelper.setDueTime(task, request.getDueType(), request.getDueDate(), request.getDueDateTime());
        Category category = null;
        if(request.getCategoryId() != null) {
            category = categoryRepository.findByIdAndUserId(request.getCategoryId(), userId)
                    .orElseThrow(() -> new AppException(ErrorStatus.CATEGORY_NOT_FOUND));
        }

        Task savedTask = taskRepository.save(task);
        return taskMapper.toTaskResponse(savedTask, category);
    }

    @Override
    public Map<LocalDate, List<TaskResponse>> getTasksByDueDateList(UUID userId, List<LocalDate> dueDates) {
        List<Task> tasks = taskRepository.findTasksByDueDateList(userId, dueDates);

        Set<UUID> categoryIds = tasks.stream()
                .map(Task::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<UUID, Category> categoryMap = categoryRepository.findAllById(categoryIds)
                .stream()
                .collect(Collectors.toMap(Category::getId, Function.identity()));

        List<TaskResponse> result = tasks.stream().map(task -> {
            TaskResponse res = taskMapper.toTaskResponse(task);
            if (task.getCategoryId() != null) {
                Category cat = categoryMap.get(task.getCategoryId());
                if (cat != null) {
                    res.setCategory(categoryMapper.toCategoryResponse(cat));
                }
            }
            return res;
        }).toList();

        return result.stream()
                .filter(task -> task.getDueDate() != null)
                .collect(Collectors.groupingBy(TaskResponse::getDueDate));
    }

    @Override
    public List<TaskResponse> filterTasks(UUID userId, String status, LocalDate dueDate,
                                          UUID categoryId, Boolean inbox) {
        List<Task> tasks;
        OffsetDateTime nowUtc = OffsetDateTime.now(ZoneOffset.UTC);

        if(Boolean.TRUE.equals(inbox)) {
            tasks = taskRepository.findInboxTasks(userId);
        } else if(dueDate != null) {
            OffsetDateTime start = TaskDueTimeHelper.startOfDay(dueDate);
            OffsetDateTime end = TaskDueTimeHelper.endOfDay(dueDate);
            tasks = taskRepository.findTasksByDueDate(userId, start, end);
        } else if ("overdue".equalsIgnoreCase(status)) {
            tasks = taskRepository.findOverdueTasks(userId, nowUtc);
        } else if ("upcoming".equalsIgnoreCase(status)) {
            tasks = taskRepository.findUpComingTasks(userId, nowUtc);
        } else if ("completed".equalsIgnoreCase(status)) {
            tasks = taskRepository.findCompletedTasks(userId);
        } else if (categoryId != null) {
            tasks = taskRepository.findTasksByCategory(userId, categoryId);
        } else {
            tasks = taskRepository.findAllByUserId(userId);
        }

        return mapTasksToResponses(tasks);
    }

    @Override
    public CountTaskResponse countTasks(UUID userId) {
        LocalDate today = LocalDate.now();
        OffsetDateTime now = OffsetDateTime.now();

        long countInbox = taskRepository.countInboxTasks(userId);
        long countToday = taskRepository.countTodayTasks(userId, today);
        long countOverdue = taskRepository.countOverdueTasks(userId, now);
        long countUpcoming = taskRepository.countUpComingTasks(userId, now);

        return CountTaskResponse.builder()
                .inbox(countInbox)
                .today(countToday)
                .overdue(countOverdue)
                .upcoming(countUpcoming)
                .build();
    }

    @Override
    @Transactional
    public TaskResponse updateTask(UUID taskId, UUID userId, TaskRequest updateTask) {
        validateDueTimeRequest(updateTask);

        Task task = loadTaskByUserId(taskId, userId);
        taskMapper.updateTaskFromDto(updateTask, task);

        Task savedTask = taskRepository.save(task);

        Category category = null;
        if (updateTask.getCategoryId() != null) {
            category = categoryRepository.findByIdAndUserId(updateTask.getCategoryId(), userId)
                    .orElseThrow(() -> new AppException(ErrorStatus.CATEGORY_NOT_FOUND));
        }

        return taskMapper.toTaskResponse(savedTask, category);
    }

    @Override
    @Transactional
    public TaskResponse patchTask(UUID taskId, UUID userId, UpdateTaskRequest updateTask) {
        Task task = loadTaskByUserId(taskId, userId);

        if(updateTask.getTitle() != null) {
            task.setTitle(updateTask.getTitle());
        }

        if(updateTask.getDescription() != null) {
            task.setDescription(updateTask.getDescription());
        }

        if(updateTask.getDueType() != null) {
            validateDueTimeRequest(updateTask);

            // Set due time based on type
            TaskDueTimeHelper.setDueTime(
                    task,
                    updateTask.getDueType(),
                    updateTask.getDueDate(),
                    updateTask.getDueDateTime()
            );
        }

        if(updateTask.getPriority() != null) {
            task.setPriority(updateTask.getPriority());
        }

        // Logic completed task
        if(updateTask.getIsCompleted() != null) {
            task.setIsCompleted(updateTask.getIsCompleted());
            if(updateTask.getIsCompleted()) {
                task.setCompletedAt(OffsetDateTime.now(ZoneOffset.UTC));
            } else {
                task.setCompletedAt(null);
            }
        }

        // Category logic
        Category category = null;
        if (updateTask.getCategoryId() != null) {
            category = categoryRepository.findByIdAndUserId(updateTask.getCategoryId(), userId)
                    .orElseThrow(() -> new AppException(ErrorStatus.CATEGORY_NOT_FOUND));
            task.setCategoryId(updateTask.getCategoryId());
        } else if (updateTask.getCategoryId() == null && updateTask.getTitle() != null) {
            // Nếu explicitly set null (distinguish từ không gửi field)
            task.setCategoryId(null);
        }

        Task savedTask = taskRepository.save(task);
        return taskMapper.toTaskResponse(savedTask, category);
    }

    @Override
    @Transactional
    public void deleteTask(UUID taskId, UUID userId) {
        Task task = loadTaskByUserId(taskId, userId);
        taskRepository.delete(task);
    }

    // ========== PRIVATE HELPER METHODS ==========

    private Task loadTaskByUserId(UUID taskId, UUID userId) {
        return taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new AppException(ErrorStatus.TASK_NOT_FOUND));
    }

    /**
     * Map list of tasks to responses with categories
     */
    private List<TaskResponse> mapTasksToResponses(List<Task> tasks) {
        Set<UUID> categoryIds = tasks.stream()
                .map(Task::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<UUID, Category> categoryMap = categoryRepository.findAllById(categoryIds)
                .stream()
                .collect(Collectors.toMap(Category::getId, Function.identity()));

        return tasks.stream().map(task -> {
            TaskResponse res = taskMapper.toTaskResponse(task);
            if (task.getCategoryId() != null) {
                Category cat = categoryMap.get(task.getCategoryId());
                if (cat != null) {
                    res.setCategory(categoryMapper.toCategoryResponse(cat));
                }
            }
            return res;
        }).toList();
    }

    /**
     * Validate due time request
     */
    private void validateDueTimeRequest(TaskRequest request) {
        if (request.getDueType() == null) {
            return;
        }

        switch (request.getDueType()) {
            case DATE -> {
                if (request.getDueDate() == null || request.getDueDateTime() != null) {
                    throw new AppException(ErrorStatus.INVALID_DUE_DATE);
                }
            }

            case DATE_TIME -> {
                if (request.getDueDateTime() == null || request.getDueDate() != null) {
                    throw new AppException(ErrorStatus.INVALID_DUE_DATE_TIME);
                }
            }

            case NONE -> {
                if (request.getDueDate() != null || request.getDueDateTime() != null) {
                    throw new AppException(ErrorStatus.INVALID_DUE_DATE_TIME);
                }
            }
        }
    }


    /**
     * Validate due time for patch request
     */
    private void validateDueTimeRequest(UpdateTaskRequest request) {
        if (request.getDueType() == null) {
            return;
        }

        if (request.getDueType() == DueType.DATE) {
            if (request.getDueDate() == null) {
                throw new AppException(ErrorStatus.INVALID_DUE_DATE);
            }
        } else if (request.getDueType() == DueType.DATE_TIME) {
            if (request.getDueDateTime() == null) {
                throw new AppException(ErrorStatus.INVALID_DUE_DATE_TIME);
            }
        }
    }
}