package com.hoangtrang.taskoserver.repository;

import com.hoangtrang.taskoserver.model.Category;
import com.hoangtrang.taskoserver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findAllByUser(User user);

    Optional<Category> findByIdAndUser(UUID id, User user);
}

