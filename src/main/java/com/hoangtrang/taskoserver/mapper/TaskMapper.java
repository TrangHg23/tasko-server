package com.hoangtrang.taskoserver.mapper;

import com.hoangtrang.taskoserver.dto.task.CreateTaskRequest;
import com.hoangtrang.taskoserver.dto.task.TaskResponse;
import com.hoangtrang.taskoserver.model.Category;
import com.hoangtrang.taskoserver.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = CategoryMapper.class)
public interface TaskMapper {

    @Mapping(target = "id", source = "task.id")
    @Mapping(target = "createdAt", source = "task.createdAt")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "isInboxTask", expression = "java(task.isInboxTask())")
    @Mapping(target = "isTodayTask", expression = "java(task.isTodayTask())")
    @Mapping(target = "isOverdue", expression = "java(task.isOverdue())")
    TaskResponse toTaskResponse(Task task, Category category);

    @Mapping(target = "isInboxTask", expression = "java(task.isInboxTask())")
    @Mapping(target = "isTodayTask", expression = "java(task.isTodayTask())")
    @Mapping(target = "isOverdue", expression = "java(task.isOverdue())")
    TaskResponse toTaskResponse(Task task);

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "categoryId", source = "request.categoryId")
    @Mapping(target = "priority", expression = "java(request.priority() != null ? request.priority() : PriorityLevel.NONE)")
    Task toTask(CreateTaskRequest request, UUID userId);

}
