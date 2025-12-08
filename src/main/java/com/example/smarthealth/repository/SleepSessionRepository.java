package com.example.smarthealth.repository;

import com.example.smarthealth.model.health.SleepSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SleepSessionRepository extends JpaRepository<SleepSession, Long> {
    List<SleepSession> findByUserIdAndStartTimeBetween(Long userId, LocalDateTime from, LocalDateTime to);
}