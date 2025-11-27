package com.hoangtrang.taskoserver.service;

import com.hoangtrang.taskoserver.dto.auth.ForgotPasswordRequest;

public interface PasswordResetService {
    void createPasswordResetToken(ForgotPasswordRequest forgotPasswordRequest);
}
