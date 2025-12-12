package com.hoangtrang.taskoserver.repository;

import com.hoangtrang.taskoserver.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

   Optional<PasswordResetToken> findByToken(String token);

   void deleteAllByExpiryDateBefore(Instant time);

}
