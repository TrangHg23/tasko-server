package com.hoangtrang.taskoserver.controller;

import com.hoangtrang.taskoserver.dto.auth.*;
import com.hoangtrang.taskoserver.dto.common.ResponseData;
import com.hoangtrang.taskoserver.dto.auth.IntrospectRequest;
import com.hoangtrang.taskoserver.dto.auth.IntrospectResponse;
import com.hoangtrang.taskoserver.dto.auth.LoginRequest;
import com.hoangtrang.taskoserver.service.AuthService;
import com.nimbusds.jose.JOSEException;
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

import java.text.ParseException;

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


    @Operation(summary= "User login", description = "Authenticate user using email and password.")
    @PostMapping("/log-in")
    public ResponseData<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        var result = authService.authenticate(request);
        return ResponseData.<LoginResponse>builder()
                .data(result)
                .build();
    }

    @Operation(summary = "Check access token validity", description = "Check an access token whether it is valid.")
    @PostMapping("/introspect")
    public ResponseData<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) {
        var result = authService.introspect(request);
        return ResponseData.<IntrospectResponse>builder()
                .data(result)
                .build();
    }


    @Operation(summary = "Log out the user", description = "Terminates the user's session and invalidates the authentication token.")
    @PostMapping("/log-out")
    public ResponseData<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authService.logout(request);
        return new ResponseData<>(HttpStatus.OK.value(), "User logout successfully");
    }

    @Operation(summary= "Refresh token", description = "Generates a new access token using a valid refresh token.")
    @PostMapping("/refresh")
    public ResponseData<RefreshResponse> refreshToken(@RequestBody RefreshRequest request) {
        var result = authService.refreshAccessToken(request.refreshToken());
        return ResponseData.<RefreshResponse>builder()
                .data(result).build();
    }
}
