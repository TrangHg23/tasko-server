package com.hoangtrang.taskoserver.mapper;

import com.hoangtrang.taskoserver.dto.task.TaskRequest;
import com.hoangtrang.taskoserver.dto.task.TaskResponse;
import com.hoangtrang.taskoserver.model.Category;
import com.hoangtrang.taskoserver.model.Task;
import com.hoangtrang.taskoserver.model.enums.DueType;
import com.hoangtrang.taskoserver.model.enums.PriorityLevel;
import com.hoangtrang.taskoserver.service.TaskDueTimeHelper;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.UUID;

@Mapper(
        componentModel = "spring",
        uses = CategoryMapper.class,
        imports = {PriorityLevel.class, DueType.class, TaskDueTimeHelper.class}
)
public interface TaskMapper {

    //response
    @Mapping(target = "id", source = "task.id")
    @Mapping(target = "createdAt", source = "task.createdAt")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "inboxTask", expression = "java(task.isInboxTask())")
    @Mapping(target = "todayTask", ignore = true)
    @Mapping(target = "overdue", ignore = true)
    @Mapping(target = "dueType", source = "task.dueType")
    @Mapping(target = "dueDate", expression = "java(TaskDueTimeHelper.extractDueDate(task))")
    @Mapping(target = "dueDateTime", expression = "java(TaskDueTimeHelper.extractDueDateTime(task))")
    TaskResponse toTaskResponse(Task task, Category category);

    @Mapping(target = "inboxTask", expression = "java(task.isInboxTask())")
    @Mapping(target = "dueType", source = "task.dueType")
    @Mapping(target = "todayTask", ignore = true)
    @Mapping(target = "overdue", ignore = true)
    @Mapping(target = "dueDate", expression = "java(TaskDueTimeHelper.extractDueDate(task))")
    @Mapping(target = "dueDateTime", expression = "java(TaskDueTimeHelper.extractDueDateTime(task))")
    TaskResponse toTaskResponse(Task task);

    //create
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "categoryId", source = "request.categoryId")
    @Mapping(target = "priority", source = "request.priority", defaultExpression = "java(getDefaultPriority())")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isCompleted", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "dueAt", ignore = true) // set in @AfterMapping
    @Mapping(target = "dueType", source = "request.dueType", defaultExpression = "java(DueType.NONE)")
    Task toTask(TaskRequest request, UUID userId);

    //update
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isCompleted", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "dueAt", ignore = true) // set in @AfterMapping
    @Mapping(target = "dueType", ignore = true)
    void updateTaskFromDto(TaskRequest request, @MappingTarget Task task);

    // after map
    @AfterMapping
    default void setDueTimeAfterCreate(TaskRequest request, @MappingTarget Task task) {
        TaskDueTimeHelper.setDueTime(
                task,
                request.getDueType(),
                request.getDueDate(),
                request.getDueDateTime()
        );
    }

    @AfterMapping
    default void setDueTimeAfterUpdate(TaskRequest request, @MappingTarget Task task) {
        if (
                request.getDueType() != null ||
                        request.getDueDate() != null ||
                        request.getDueDateTime() != null
        ) {
            TaskDueTimeHelper.setDueTime(
                    task,
                    request.getDueType(),
                    request.getDueDate(),
                    request.getDueDateTime()
            );
        }
    }

    default PriorityLevel getDefaultPriority() {
        return PriorityLevel.LOW;
    }
}
