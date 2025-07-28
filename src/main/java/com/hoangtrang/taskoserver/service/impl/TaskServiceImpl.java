package com.hoangtrang.taskoserver.service.impl;

import com.hoangtrang.taskoserver.dto.task.CountTaskResponse;
import com.hoangtrang.taskoserver.dto.task.TaskRequest;
import com.hoangtrang.taskoserver.dto.task.TaskResponse;
import com.hoangtrang.taskoserver.dto.task.UpdateTaskRequest;
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

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskServiceImpl implements TaskService {
    TaskRepository taskRepository;
    CategoryRepository categoryRepository;
    TaskMapper taskMapper;

    @Override
    public TaskResponse createTask(TaskRequest request, UUID userId) {
        Task task = taskMapper.toTask(request, userId);

        Category category = null;
        if(request.categoryId() != null) {
            category = categoryRepository.findByIdAndUserId(request.categoryId(), userId)
                    .orElseThrow(() -> new AppException(ErrorStatus.CATEGORY_NOT_FOUND));
        }

        Task savedTask = taskRepository.save(task);
        return taskMapper.toTaskResponse(savedTask, category);
    }

    @Override
    public Map<LocalDate, List<TaskResponse>> getTasksByDueDateList(UUID userId, List<LocalDate> dueDates) {
        List<Task> tasks = taskRepository.findTasksByDueDateList(userId, dueDates);
        return tasks.stream()
                .collect(Collectors.groupingBy(
                        Task::getDueDate,
                        Collectors.mapping(taskMapper::toTaskResponse, Collectors.toList())
                ));
    }

    @Override
    public List<TaskResponse> filterTasks(UUID userId, String status,LocalDate dueDate, UUID categoryId, Boolean inbox) {
        List<Task> tasks;
        LocalDate today = LocalDate.now();

        if(Boolean.TRUE.equals(inbox)) {
            tasks = taskRepository.findInboxTasks(userId);
        } else if(dueDate != null) {
            tasks = taskRepository.findTasksByDueDate(userId,dueDate);
        } else if ("overdue".equalsIgnoreCase(status)) {
            tasks = taskRepository.findOverdueTasks(userId, today);
        } else if ("upcoming".equalsIgnoreCase(status)) {
            tasks = taskRepository.findUpComingTasks(userId, today);
        } else if ("completed".equalsIgnoreCase(status)) {
            tasks = taskRepository.findCompletedTasks(userId);
        } else if (categoryId != null) {
            tasks = taskRepository.findTasksByCategory(userId, categoryId);
        } else {
            tasks = taskRepository.findALlByUserId(userId);
        }
        return tasks.stream().map(taskMapper::toTaskResponse).collect(Collectors.toList());
    }

    @Override
    public CountTaskResponse countTasks(UUID userId) {
        LocalDate today = LocalDate.now();
        long countInbox = taskRepository.countInboxTasks(userId);
        long countToday = taskRepository.countTodayTasks(userId, today);
        long countOverdue = taskRepository.countOverdueTasks(userId, today);
        long countUpcoming = taskRepository.countUpComingTasks(userId, today);

        return CountTaskResponse.builder()
                .inbox(countInbox)
                .today(countToday)
                .overdue(countOverdue)
                .upcoming(countUpcoming)
                .build();
    }

    private Task loadTaskByUserId(UUID taskId, UUID userId) {
        return taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new AppException(ErrorStatus.TASK_NOT_FOUND));
    }
    @Override
    public TaskResponse updateTask(UUID taskId, UUID userId, TaskRequest updateTask) {
        Task task = loadTaskByUserId(taskId, userId);
        taskMapper.updateTaskFromDto(updateTask, task);
        Task savedTask = taskRepository.save(task);
        return taskMapper.toTaskResponse(savedTask);
    }

    @Override
    public TaskResponse patchTask(UUID taskId, UUID userId, UpdateTaskRequest updateTask) {
        Task task = loadTaskByUserId(taskId, userId);
        if(updateTask.getTitle() != null) task.setTitle(updateTask.getTitle());
        if(updateTask.getDescription() != null) task.setDescription(updateTask.getDescription());
        if(updateTask.getDueDate() != null) task.setDueDate(updateTask.getDueDate());
        if(updateTask.getPriority() != null) task.setPriority(updateTask.getPriority());

        // logic with completed task
        if(updateTask.getIsCompleted() != null) {
            task.setIsCompleted(updateTask.getIsCompleted());
            if(updateTask.getIsCompleted()) {
                task.setCompletedAt(OffsetDateTime.now(ZoneOffset.UTC));
            } else {
                task.setCompletedAt(null);
            }
        }

        Task savedTask = taskRepository.save(task);
        return taskMapper.toTaskResponse(savedTask);
    }

    @Override
    public void deleteTask(UUID taskId, UUID userId) {
        Task task = loadTaskByUserId(taskId, userId);
        taskRepository.delete(task);
    }
}
