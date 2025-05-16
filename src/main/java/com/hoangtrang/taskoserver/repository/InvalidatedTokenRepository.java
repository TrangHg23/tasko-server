package com.hoangtrang.taskoserver.repository;

import com.hoangtrang.taskoserver.model.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {
}
