package com.hoangtrang.taskoserver.controller;

import com.hoangtrang.taskoserver.dto.request.RegisterRequest;
import com.hoangtrang.taskoserver.dto.response.RegisterResponse;
import com.hoangtrang.taskoserver.dto.response.ResponseData;
import com.hoangtrang.taskoserver.model.User;
import com.hoangtrang.taskoserver.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Validated
@Tag(name = "Auth Controller")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    AuthService authService;

    @Operation(summary = "Register new account", description = "Creates a new user account using email and password.")
    @PostMapping("/sign-up")
    public ResponseData<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return new ResponseData<>(HttpStatus.CREATED.value(), "User registered successfully", response);
    }
}
