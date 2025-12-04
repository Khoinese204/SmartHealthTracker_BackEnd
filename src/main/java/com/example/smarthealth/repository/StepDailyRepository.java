package com.example.smarthealth.repository;

import com.example.smarthealth.model.health.StepDaily;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface StepDailyRepository extends JpaRepository<StepDaily, Long> {
    List<StepDaily> findByUserIdAndDateBetween(Long userId, LocalDate from, LocalDate to);
}
