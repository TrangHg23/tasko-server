package com.hoangtrang.taskoserver.service;

import com.hoangtrang.taskoserver.dto.request.IntrospectRequest;
import com.hoangtrang.taskoserver.dto.request.LoginRequest;
import com.hoangtrang.taskoserver.dto.request.LogoutRequest;
import com.hoangtrang.taskoserver.dto.request.RegisterRequest;
import com.hoangtrang.taskoserver.dto.response.IntrospectResponse;
import com.hoangtrang.taskoserver.dto.response.LoginResponse;
import com.hoangtrang.taskoserver.dto.response.RegisterResponse;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);

    LoginResponse authenticate(LoginRequest request);

    IntrospectResponse introspect(IntrospectRequest request) throws ParseException, JOSEException;

    void logout(LogoutRequest request) throws ParseException, JOSEException;

}
