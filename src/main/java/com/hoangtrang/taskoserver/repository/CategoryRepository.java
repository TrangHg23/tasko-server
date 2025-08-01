package com.hoangtrang.taskoserver.repository;

import com.hoangtrang.taskoserver.dto.category.CategoryResponse;
import com.hoangtrang.taskoserver.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    @Query("""
        SELECT new com.hoangtrang.taskoserver.dto.category.CategoryResponse(
            c.id, c.name, COUNT(t)
        )
        FROM Category c
        LEFT JOIN Task t ON t.categoryId = c.id AND t.userId = :userId AND t.isCompleted = false
        WHERE c.user.id = :userId
        GROUP BY c.id, c.name, c.createdAt
        ORDER BY c.createdAt
    """)
    List<CategoryResponse> findAllCategories(@Param("userId") UUID userId);

    Optional<Category> findByIdAndUserId(UUID id, UUID userId);
}

