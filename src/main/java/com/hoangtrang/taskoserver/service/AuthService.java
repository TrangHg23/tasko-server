package com.hoangtrang.taskoserver.service;

import com.hoangtrang.taskoserver.dto.request.RegisterRequest;
import com.hoangtrang.taskoserver.dto.response.RegisterResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
}
