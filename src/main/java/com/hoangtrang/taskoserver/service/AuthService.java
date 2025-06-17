package com.hoangtrang.taskoserver.service;

import com.hoangtrang.taskoserver.dto.request.*;
import com.hoangtrang.taskoserver.dto.response.*;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);

    LoginResponse authenticate(LoginRequest request);

    IntrospectResponse introspect(IntrospectRequest request);

    void logout(LogoutRequest request) throws ParseException, JOSEException;

    RefreshResponse refreshAccessToken(String refreshToken);

    UserInfo getCurrentUserInfo(String email);
}
