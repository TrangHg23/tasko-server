package com.hoangtrang.taskoserver.repository;

import com.hoangtrang.taskoserver.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    // Lấy tất cả task trong inbox (categoryId = null và chưa completed)
    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.categoryId IS NULL AND t.isCompleted = false ORDER BY t.createdAt DESC")
    List<Task> findInboxTasks(@Param("userId") UUID userId);

    // Lấy task today (tính cả task có category)
    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.dueDate =:today AND t.isCompleted = false ORDER BY t.priority DESC, t.createdAt DESC")
    List<Task> findTodayTasks(@Param("userId") UUID userId, @Param("today")LocalDate today);

    // Lấy task overdue
    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.dueDate < :today AND t.isCompleted = false ORDER BY t.dueDate ASC")
    List<Task> findOverdueTasks(@Param("userId") UUID userId, @Param("today") LocalDate today);

    // Lấy upcoming task
    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.dueDate > :today AND t.isCompleted = false ORDER BY t.dueDate ASC")
    List<Task> findUpComingTasks(@Param("userId") UUID userId, @Param("today") LocalDate today);

    // Lấy task theo category
    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.categoryId = :categoryId AND t.isCompleted = false ORDER BY t.createdAt DESC")
    List<Task> findTasksByCategory(@Param("userId") UUID userId, @Param("categoryId") UUID categoryId);

    // Lấy completed tasks
    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.isCompleted = true ORDER BY t.completedAt DESC")
    List<Task> findCompletedTasks(@Param("userId") UUID userId);

    // Count task
    @Query("SELECT COUNT(t) FROM Task t WHERE t.userId = :userId AND t.categoryId IS NULL AND t.isCompleted = false")
    long countInboxTasks(@Param("userId") UUID userId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.userId = :userId AND t.dueDate = :today AND t.isCompleted = false")
    long countTodayTasks(@Param("userId") UUID userId, @Param("today") LocalDate today);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.userId = :userId AND t.dueDate < :today AND t.isCompleted = false")
    long countOverdueTasks(@Param("userId") UUID userId, @Param("today") LocalDate today);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.userId = :userId AND t.dueDate > :today AND t.isCompleted = false")
    long countUpComingTasks(@Param("userId") UUID userId, @Param("today") LocalDate today);

    long countByUserIdAndCategoryId(UUID userId, UUID categoryId);

    Optional<Task> findByIdAndUserId(UUID id, UUID userId);

    List<Task> findALlByUserId(UUID userId);
}
