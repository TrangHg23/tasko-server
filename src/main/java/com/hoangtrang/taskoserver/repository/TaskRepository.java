package com.hoangtrang.taskoserver.repository;

import com.hoangtrang.taskoserver.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    // Inbox
    @Query("SELECT t FROM Task t WHERE t.userId = :userId " +
            "AND t.categoryId IS NULL AND t.isCompleted = false " +
            "ORDER BY t.createdAt")
    List<Task> findInboxTasks(@Param("userId") UUID userId);

    // Tasks by date
    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.isCompleted = false " +
            "AND t.dueType != 'NONE' " +
            "AND t.dueAt >= :startOfDay AND t.dueAt <= :endOfDay " +
            "ORDER BY t.dueAt")
    List<Task> findTasksByDueDate(
            @Param("userId") UUID userId,
            @Param("startOfDay") OffsetDateTime startOfDay,
            @Param("endOfDay") OffsetDateTime endOfDay);

    // Tasks by date list
    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.isCompleted = false " +
            "AND t.dueType != 'NONE' " +
            "AND CAST(t.dueAt AS date) IN :dates " +
            "ORDER BY t.dueAt")
    List<Task> findTasksByDueDateList(@Param("userId") UUID userId, @Param("dates") List<LocalDate> dates);

    // Overdue
    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.isCompleted = false " +
            "AND t.dueType != 'NONE' " +
            "AND t.dueAt < :now " +
            "ORDER BY t.dueAt")
    List<Task> findOverdueTasks(@Param("userId") UUID userId, @Param("now") OffsetDateTime now);

    // Upcoming
    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.isCompleted = false " +
            "AND t.dueType != 'NONE' " +
            "AND t.dueAt > :now " +
            "ORDER BY t.dueAt")
    List<Task> findUpComingTasks(@Param("userId") UUID userId, @Param("now") OffsetDateTime now);

    // By category
    @Query("SELECT t FROM Task t WHERE t.userId = :userId " +
            "AND t.categoryId = :categoryId AND t.isCompleted = false " +
            "ORDER BY t.dueAt NULLS LAST, t.createdAt")
    List<Task> findTasksByCategory(@Param("userId") UUID userId, @Param("categoryId") UUID categoryId);

    // Completed
    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.isCompleted = true " +
            "ORDER BY t.completedAt DESC")
    List<Task> findCompletedTasks(@Param("userId") UUID userId);

    // Counts
    @Query("SELECT COUNT(t) FROM Task t WHERE t.userId = :userId " +
            "AND t.categoryId IS NULL AND t.isCompleted = false")
    long countInboxTasks(@Param("userId") UUID userId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.userId = :userId AND t.isCompleted = false " +
            "AND t.dueType != 'NONE' " +
            "AND CAST(t.dueAt AS date) = :today")
    long countTodayTasks(@Param("userId") UUID userId, @Param("today") LocalDate today);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.userId = :userId AND t.isCompleted = false " +
            "AND t.dueType != 'NONE' " +
            "AND t.dueAt < :now")
    long countOverdueTasks(@Param("userId") UUID userId, @Param("now") OffsetDateTime now);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.userId = :userId AND t.isCompleted = false " +
            "AND t.dueType != 'NONE' " +
            "AND t.dueAt > :now")
    long countUpComingTasks(@Param("userId") UUID userId, @Param("now") OffsetDateTime now);

    Optional<Task> findByIdAndUserId(UUID id, UUID userId);

    List<Task> findAllByUserId(UUID userId);
}
