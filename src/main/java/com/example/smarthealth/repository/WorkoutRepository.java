package com.example.smarthealth.repository;

import com.example.smarthealth.model.health.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface WorkoutRepository extends JpaRepository<WorkoutSession, Long> {

    List<WorkoutSession> findAllByUserId(Long userId);

    @Query("""
                select coalesce(sum(w.calories), 0)
                from WorkoutSession w
                where function('date', w.startTime) = :date
            """)
    Integer sumCaloriesByDate(@Param("date") LocalDate date);

    @Query("""
                select count(w)
                from WorkoutSession w
                where function('date', w.startTime) = :date
            """)
    Long countWorkoutsByDate(@Param("date") LocalDate date);

    @Query("""
                select w from WorkoutSession w
                where w.user.id = :userId
                  and function('date', w.startTime) between :from and :to
                order by w.startTime asc
            """)
    List<WorkoutSession> findByUserIdAndStartDateBetween(
            @Param("userId") Long userId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);
}
