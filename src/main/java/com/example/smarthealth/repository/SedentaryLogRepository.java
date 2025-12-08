package com.example.smarthealth.repository;

import com.example.smarthealth.model.health.SedentaryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SedentaryLogRepository extends JpaRepository<SedentaryLog, Long> {
    List<SedentaryLog> findByUserIdAndStartTimeAfter(Long userId, LocalDateTime startTime);
}