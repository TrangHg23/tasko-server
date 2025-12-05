package com.hoangtrang.taskoserver.service.impl;

import com.hoangtrang.taskoserver.dto.auth.ForgotPasswordRequest;
import com.hoangtrang.taskoserver.dto.auth.ResetPasswordRequest;
import com.hoangtrang.taskoserver.exception.AppException;
import com.hoangtrang.taskoserver.exception.ErrorStatus;
import com.hoangtrang.taskoserver.model.PasswordResetToken;
import com.hoangtrang.taskoserver.model.User;
import com.hoangtrang.taskoserver.repository.PasswordResetTokenRepository;
import com.hoangtrang.taskoserver.repository.UserRepository;
import com.hoangtrang.taskoserver.service.EmailService;
import com.hoangtrang.taskoserver.service.PasswordResetService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PasswordResetServiceImpl implements PasswordResetService {
    UserRepository userRepository;
    PasswordResetTokenRepository tokenRepository;
    EmailService emailService;
    PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void createPasswordResetToken(ForgotPasswordRequest forgotPasswordRequest) {
        User user = userRepository.findByEmail(forgotPasswordRequest.email()).orElse(null);
        if(user == null) return;

        PasswordResetToken resetToken = tokenRepository.findByUser_Id(user.getId())
                .map(existing -> {
                    existing.setToken(UUID.randomUUID().toString());
                    existing.setExpiryDate(Instant.now().plus(30, ChronoUnit.MINUTES));
                    return existing;
                })
                .orElse(
                    PasswordResetToken.builder()
                        .token(UUID.randomUUID().toString())
                        .expiryDate(Instant.now().plus(30, ChronoUnit.MINUTES))
                        .user(user)
                        .build()
                );

        tokenRepository.save(resetToken);

        emailService.sendPasswordResetEmail(user.getEmail(), resetToken.getToken());

    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken token = tokenRepository.findByToken(request.token())
                .orElseThrow(() -> new AppException(ErrorStatus.TOKEN_NOT_FOUND));

        validateExpiry(token);
        validateNotUsed(token);
        updateUserPassword(token.getUser(), request.newPassword());
        invalidateToken(token);
    }

    private void validateExpiry(PasswordResetToken token) {
        if(token.getExpiryDate().isBefore(Instant.now())) {
            throw new AppException(ErrorStatus.TOKEN_EXPIRED);
        }
    }

    private void validateNotUsed(PasswordResetToken token) {
        if(token.isUsed()) throw new AppException(ErrorStatus.TOKEN_ALREADY_USED);
    }

    private void updateUserPassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private void invalidateToken(PasswordResetToken token) {
        token.setUsed(true);
        tokenRepository.save(token);
    }
}
