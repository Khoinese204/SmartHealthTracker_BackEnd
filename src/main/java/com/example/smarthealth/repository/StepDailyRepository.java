package com.example.smarthealth.repository;

import com.example.smarthealth.model.health.StepDaily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StepDailyRepository extends JpaRepository<StepDaily, Long> {
    Optional<StepDaily> findByUserIdAndDate(Long userId, LocalDate date);

    List<StepDaily> findByUserIdAndDateBetween(Long userId, LocalDate from, LocalDate to);

    @Query("select coalesce(sum(s.totalSteps), 0) from StepDaily s where s.date = :date")
    Long sumStepsByDate(@Param("date") LocalDate date);

    @Query("select s from StepDaily s where s.user.id = :userId order by s.date asc")
    java.util.List<StepDaily> findAllByUserId(@Param("userId") Long userId);
}
