package com.hoangtrang.taskoserver.controller;

import com.hoangtrang.taskoserver.config.security.CustomUserDetails;
import com.hoangtrang.taskoserver.dto.common.ResponseData;
import com.hoangtrang.taskoserver.dto.task.CountTaskResponse;
import com.hoangtrang.taskoserver.dto.task.TaskRequest;
import com.hoangtrang.taskoserver.dto.task.TaskResponse;
import com.hoangtrang.taskoserver.dto.task.UpdateTaskRequest;
import com.hoangtrang.taskoserver.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("api/tasks")
@Validated
@Slf4j
@Tag(name = "Task Controller")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskController {

    TaskService taskService;

    @Operation(summary = "Create a new task")
    @PostMapping
    public ResponseData<TaskResponse> createTask(
            @Valid
            @RequestBody TaskRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        TaskResponse response = taskService.createTask(request, userDetails.user().getId());
        return new ResponseData<>(HttpStatus.CREATED.value(), "Created task successfully", response);
    }

    @Operation(
            summary = "Get tasks with filters",
            description = "Retrieve tasks by applying only one filter at a time: inbox=true, categoryId, dueDate, or status (overdue, today, upcoming, completed)."
    )
    @GetMapping
    public ResponseData<List<TaskResponse>> getFilterTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate dueDate,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required= false) Boolean inbox,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<TaskResponse> tasks = taskService.filterTasks(
                userDetails.user().getId(),
                status,
                dueDate,
                categoryId,
                inbox
        );
        return new ResponseData<>(HttpStatus.OK.value(), "Get tasks successfully", tasks);
    }

    @Operation(
            summary = "Get tasks grouped by due dates",
            description = "Return tasks organized by due dates. You can optionally filter by a list of specific dueDates."
    )
    @GetMapping("/due-date-groups")
    public ResponseData<Map<LocalDate, List<TaskResponse>>> getTaskByDueDates(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) List<LocalDate> dueDates
    ) {
        Map<LocalDate, List<TaskResponse>> result = taskService.getTasksByDueDateList(userDetails.user().getId(), dueDates);
        return new ResponseData<>(HttpStatus.OK.value(), "Get tasks successfully", result);
    }

    @Operation(
            summary = "Get task counts by type",
            description = "Returns the number of tasks grouped by type: inbox, today, overdue, upcoming."
    )
    @GetMapping("/count")
    public ResponseData<CountTaskResponse> countTasks(@AuthenticationPrincipal CustomUserDetails userDetails) {
        CountTaskResponse response = taskService.countTasks(userDetails.user().getId());
        return new ResponseData<>(HttpStatus.OK.value(), "Count tasks successfully", response);
    }

    @Operation(summary="Update task")
    @PutMapping("/{id}")
    public ResponseData<TaskResponse> updateTask(
            @PathVariable("id") UUID taskId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody TaskRequest updateTask
    ) {
        TaskResponse response = taskService.updateTask(taskId, userDetails.user().getId(), updateTask);
        return new ResponseData<>(HttpStatus.OK.value(), "Updated task successfully", response);
    }

    @Operation(summary="Update partial task")
    @PatchMapping("/{id}")
    public ResponseData<TaskResponse> updatePartialTask(
            @PathVariable("id") UUID taskId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UpdateTaskRequest updateTask
    ){
        TaskResponse response = taskService.patchTask(taskId, userDetails.user().getId(), updateTask);
        return new ResponseData<>(HttpStatus.OK.value(), "Updated task successfully", response);
    }

    @Operation(summary="Delete task")
    @DeleteMapping("/{id}")
    public ResponseData<Void> deleteTask(@PathVariable("id") UUID taskId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        taskService.deleteTask(taskId, userDetails.user().getId());
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Deleted task successfully");
    }



}
