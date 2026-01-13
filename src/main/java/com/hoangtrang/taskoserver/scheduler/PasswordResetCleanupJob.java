package com.hoangtrang.taskoserver.scheduler;

import com.hoangtrang.taskoserver.repository.PasswordResetTokenRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PasswordResetCleanupJob {
    PasswordResetTokenRepository tokenRepository;

    @Transactional
    @Scheduled(cron = "0 0 * * * *") // run every hour
    public void cleanupExpiredTokens() {
        Instant now = Instant.now();
        tokenRepository.deleteAllByExpiryDateBefore(now.minus(1, ChronoUnit.DAYS));
    }
}
