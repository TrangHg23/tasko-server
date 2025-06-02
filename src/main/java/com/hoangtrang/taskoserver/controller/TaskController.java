package com.hoangtrang.taskoserver.controller;

import com.hoangtrang.taskoserver.config.security.CustomUserDetails;
import com.hoangtrang.taskoserver.dto.common.ResponseData;
import com.hoangtrang.taskoserver.dto.task.CreateTaskRequest;
import com.hoangtrang.taskoserver.dto.task.TaskResponse;
import com.hoangtrang.taskoserver.service.TaskService;
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


@RestController
@RequestMapping("api/tasks")
@Validated
@Slf4j
@Tag(name = "Tasks Controller")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskController {

    TaskService taskService;

    @PostMapping
    public ResponseData<TaskResponse> createTask(
            @Valid
            @RequestBody CreateTaskRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        TaskResponse response = taskService.createTask(request, userDetails.user().getId());
        return new ResponseData<>(HttpStatus.CREATED.value(), "Created task successfully", response);
    }

}
