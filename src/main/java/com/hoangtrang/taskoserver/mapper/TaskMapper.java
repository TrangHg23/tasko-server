package com.hoangtrang.taskoserver.mapper;

import com.hoangtrang.taskoserver.dto.task.TaskRequest;
import com.hoangtrang.taskoserver.dto.task.TaskResponse;
import com.hoangtrang.taskoserver.model.Category;
import com.hoangtrang.taskoserver.model.Task;
import com.hoangtrang.taskoserver.model.enums.PriorityLevel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = CategoryMapper.class, imports = {PriorityLevel.class})
public interface TaskMapper {

    @Mapping(target = "id", source = "task.id")
    @Mapping(target = "createdAt", source = "task.createdAt")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "inboxTask", expression = "java(task.isInboxTask())")
    @Mapping(target = "todayTask", expression = "java(task.isTodayTask())")
    @Mapping(target = "overdue", expression = "java(task.isOverdue())")
    TaskResponse toTaskResponse(Task task, Category category);

    @Mapping(target = "inboxTask", expression = "java(task.isInboxTask())")
    @Mapping(target = "todayTask", expression = "java(task.isTodayTask())")
    @Mapping(target = "overdue", expression = "java(task.isOverdue())")
    TaskResponse toTaskResponse(Task task);

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "categoryId", source = "request.categoryId")
    @Mapping(target = "priority", source = "request.priority", defaultExpression = "java(getDefaultPriority())")
    Task toTask(TaskRequest request, UUID userId);

    default PriorityLevel getDefaultPriority() {
        return PriorityLevel.LOW;
    }

    void updateTaskFromDto(TaskRequest dto, @MappingTarget Task task);
}
