package com.hoangtrang.taskoserver.service;

import com.hoangtrang.taskoserver.dto.auth.*;
import com.hoangtrang.taskoserver.dto.auth.IntrospectRequest;
import com.hoangtrang.taskoserver.dto.auth.IntrospectResponse;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);

    LoginResponse authenticate(LoginRequest request);

    IntrospectResponse introspect(IntrospectRequest request);

    void logout(LogoutRequest request) throws ParseException, JOSEException;

    RefreshResponse refreshAccessToken(String refreshToken);
}
