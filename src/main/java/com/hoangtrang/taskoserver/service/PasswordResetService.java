package com.hoangtrang.taskoserver.service;

import com.hoangtrang.taskoserver.dto.auth.ForgotPasswordRequest;
import com.hoangtrang.taskoserver.dto.auth.ResetPasswordRequest;

public interface PasswordResetService {
    void createPasswordResetToken(ForgotPasswordRequest forgotPasswordRequest);

    void resetPassword(ResetPasswordRequest resetPasswordRequest);
}
