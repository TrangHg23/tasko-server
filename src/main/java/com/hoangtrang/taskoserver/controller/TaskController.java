package com.hoangtrang.taskoserver.controller;

import com.hoangtrang.taskoserver.config.security.CustomUserDetails;
import com.hoangtrang.taskoserver.dto.common.ResponseData;
import com.hoangtrang.taskoserver.dto.task.CountTaskResponse;
import com.hoangtrang.taskoserver.dto.task.CreateTaskRequest;
import com.hoangtrang.taskoserver.dto.task.TaskResponse;
import com.hoangtrang.taskoserver.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
            @RequestBody CreateTaskRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        TaskResponse response = taskService.createTask(request, userDetails.user().getId());
        return new ResponseData<>(HttpStatus.CREATED.value(), "Created task successfully", response);
    }

    @Operation(
            summary = "Get tasks with filters",
            description = "Retrieve tasks by applying only one filter at a time: inbox=true, categoryId, due (today), or status (overdue, upcoming, completed)."
    )
    @GetMapping
    public ResponseData<List<TaskResponse>> getFilterTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String due,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required= false) Boolean inbox,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<TaskResponse> tasks = taskService.filterTasks(
                userDetails.user().getId(),
                status,
                due,
                categoryId,
                inbox
        );
        return new ResponseData<>(HttpStatus.OK.value(), "Get tasks successfully", tasks);
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

}
