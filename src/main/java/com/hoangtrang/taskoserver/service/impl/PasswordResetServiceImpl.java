package com.hoangtrang.taskoserver.service.impl;

import com.hoangtrang.taskoserver.dto.auth.ForgotPasswordRequest;
import com.hoangtrang.taskoserver.exception.AppException;
import com.hoangtrang.taskoserver.exception.ErrorStatus;
import com.hoangtrang.taskoserver.model.PasswordResetToken;
import com.hoangtrang.taskoserver.model.User;
import com.hoangtrang.taskoserver.repository.PasswordResetTokenRepository;
import com.hoangtrang.taskoserver.repository.UserRepository;
import com.hoangtrang.taskoserver.service.PasswordResetService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PasswordResetServiceImpl implements PasswordResetService {
    UserRepository userRepository;
    PasswordResetTokenRepository tokenRepository;

    @Transactional
    @Override
    public void createPasswordResetToken(ForgotPasswordRequest forgotPasswordRequest) {
        User user = userRepository.findByEmail(forgotPasswordRequest.email()).orElse(null);
        if(user == null) return;

        PasswordResetToken resetToken = tokenRepository.findByUser_Id(user.getId())
                .map(existing -> {
                    existing.setToken(UUID.randomUUID().toString());
                    existing.setExpiryDate(OffsetDateTime.now().plusMinutes(30));
                    return existing;
                })
                .orElse(
                    PasswordResetToken.builder()
                        .token(UUID.randomUUID().toString())
                        .expiryDate(OffsetDateTime.now().plusMinutes(30))
                        .user(user)
                        .build()
                );

        tokenRepository.save(resetToken);

    }
}
