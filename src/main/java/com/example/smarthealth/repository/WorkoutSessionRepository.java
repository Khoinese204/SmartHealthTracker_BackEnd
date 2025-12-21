package com.example.smarthealth.repository;

import com.example.smarthealth.dto.health.WorkoutStatsResponse;
import com.example.smarthealth.model.health.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {

    @Query("SELECT new com.example.smarthealth.dto.health.WorkoutStatsResponse(" +
           "COUNT(w), " +
           "COALESCE(SUM(w.durationSeconds), 0L), " +
           "COALESCE(SUM(w.distanceMeters), 0), " +
           "COALESCE(SUM(w.calories), 0L)) " +
           "FROM WorkoutSession w " +
           "WHERE w.user.id = :userId " +
           "AND w.startTime BETWEEN :start AND :end")
    WorkoutStatsResponse getStats(@Param("userId") Long userId, 
                                  @Param("start") LocalDateTime start, 
                                  @Param("end") LocalDateTime end);
    List<WorkoutSession> findByUserIdOrderByStartTimeDesc(Long userId);

    List<WorkoutSession> findByUserIdAndStartTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);
}