package com.hoangtrang.taskoserver.service;

import com.hoangtrang.taskoserver.dto.auth.ForgotPasswordRequest;
import com.hoangtrang.taskoserver.dto.auth.LoginResponse;
import com.hoangtrang.taskoserver.dto.auth.ResetPasswordRequest;

public interface PasswordResetService {
    void createPasswordResetToken(ForgotPasswordRequest forgotPasswordRequest);

    LoginResponse resetPassword(ResetPasswordRequest resetPasswordRequest);
}
