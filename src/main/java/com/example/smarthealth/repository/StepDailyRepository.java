package com.example.smarthealth.repository;

import com.example.smarthealth.model.health.StepDaily;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StepDailyRepository extends JpaRepository<StepDaily, Long> {
    Optional<StepDaily> findByUserIdAndDate(Long userId, LocalDate date);

    List<StepDaily> findByUserIdAndDateBetween(Long userId, LocalDate from, LocalDate to);
}
