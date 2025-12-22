package com.example.smarthealth.repository;

import com.example.smarthealth.model.health.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {
    List<WorkoutSession> findByUserIdOrderByStartTimeDesc(Long userId);

    List<WorkoutSession> findByUserIdAndStartTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);

    @Query("""
                select count(w)
                from WorkoutSession w
                where w.user.id = :userId
                  and w.startTime between :from and :to
            """)
    long countWorkoutsBetween(@Param("userId") Long userId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    @Query("""
                select count(w)
                from WorkoutSession w
                where w.user.id = :userId
            """)
    long countTotalWorkouts(@Param("userId") Long userId);

    List<WorkoutSession> findByUser_IdOrderByStartTimeDesc(Long userId);

    List<WorkoutSession> findByUser_IdAndStartTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);

}