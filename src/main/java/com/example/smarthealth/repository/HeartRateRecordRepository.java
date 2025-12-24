package com.example.smarthealth.repository;

import com.example.smarthealth.dto.health.HeartRateStatsResponse;
import com.example.smarthealth.model.health.HeartRateRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HeartRateRecordRepository extends JpaRepository<HeartRateRecord, Long> {

    @Query("SELECT new com.example.smarthealth.dto.health.HeartRateStatsResponse(" +
            "COALESCE(MIN(h.bpm), 0), " +
            "COALESCE(MAX(h.bpm), 0), " +
            "COALESCE(AVG(h.bpm), 0.0), " +
            "COUNT(h)) " +
            "FROM HeartRateRecord h " +
            "WHERE h.user.id = :userId " +
            "AND h.measuredAt BETWEEN :start AND :end")
    HeartRateStatsResponse getStats(@Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    List<HeartRateRecord> findByUserIdOrderByMeasuredAtDesc(Long userId);

    List<HeartRateRecord> findByUserIdAndMeasuredAtBetween(Long userId, LocalDateTime start, LocalDateTime end);

    List<HeartRateRecord> findAllByUserIdAndMeasuredAtBetweenOrderByMeasuredAtDesc(
            Long userId,
            LocalDateTime start,
            LocalDateTime end);
}