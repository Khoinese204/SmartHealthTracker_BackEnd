package com.example.smarthealth.repository;

import com.example.smarthealth.dto.health.SleepStatsResponse;
import com.example.smarthealth.model.health.SleepSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SleepSessionRepository extends JpaRepository<SleepSession, Long> {

    @Query("SELECT new com.example.smarthealth.dto.health.SleepStatsResponse(" +
            "COUNT(s), " +
            "COALESCE(AVG(s.durationMinutes), 0.0), " +
            "COALESCE(SUM(s.durationMinutes), 0L)) " +
            "FROM SleepSession s " +
            "WHERE s.user.id = :userId " +
            "AND s.startTime BETWEEN :start AND :end")
    SleepStatsResponse getStats(@Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    List<SleepSession> findByUserIdAndStartTimeBetween(Long userId, LocalDateTime from, LocalDateTime to);

    List<SleepSession> findAllByUserIdAndStartTimeBetweenOrderByStartTimeDesc(
            Long userId,
            LocalDateTime start,
            LocalDateTime end);
}