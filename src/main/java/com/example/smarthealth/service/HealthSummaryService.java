package com.example.smarthealth.service;

import com.example.smarthealth.config.CurrentUserService;
import com.example.smarthealth.dto.health.DailySummaryDto;
import com.example.smarthealth.model.auth.User;
import com.example.smarthealth.model.health.HeartRateRecord;
import com.example.smarthealth.model.health.SleepSession;
import com.example.smarthealth.model.health.StepDaily;
import com.example.smarthealth.model.health.WorkoutSession;
import com.example.smarthealth.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.IntSummaryStatistics;

@Service
@RequiredArgsConstructor
public class HealthSummaryService {

    private final StepDailyRepository stepRepository;
    private final SleepSessionRepository sleepRepository;
    private final WorkoutSessionRepository workoutRepository;
    private final HeartRateRecordRepository heartRateRepository;
    private final CurrentUserService currentUserService;

    public DailySummaryDto getDailySummary(LocalDate date) {
        User user = currentUserService.getCurrentUser();
        Long userId = user.getId();

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        StepDaily stepDaily = stepRepository.findByUserIdAndDate(userId, date)
                .orElse(StepDaily.builder().totalSteps(0).build());
        int steps = stepDaily.getTotalSteps();

        List<SleepSession> sleeps = sleepRepository.findByUserIdAndStartTimeBetween(userId, startOfDay, endOfDay);
        int totalSleepMinutes = sleeps.stream().mapToInt(SleepSession::getDurationMinutes).sum();

        List<WorkoutSession> workouts = workoutRepository.findByUserIdAndStartTimeBetween(userId, startOfDay, endOfDay);
        int workoutCount = workouts.size();
        int workoutDuration = workouts.stream().mapToInt(w -> w.getDurationSeconds() / 60).sum();
        int workoutCalories = workouts.stream().mapToInt(w -> w.getCalories() != null ? w.getCalories() : 0).sum();

        List<HeartRateRecord> heartRates = heartRateRepository.findByUserIdAndMeasuredAtBetween(userId, startOfDay,
                endOfDay);
        IntSummaryStatistics hrStats = heartRates.stream().mapToInt(HeartRateRecord::getBpm).summaryStatistics();

        double stepCalories = steps * 0.04;
        double totalCalories = stepCalories + workoutCalories;
        double distanceKm = steps * 0.00076;

        int score = calculateHealthScore(steps, totalSleepMinutes, workoutCount);

        return DailySummaryDto.builder()
                .date(date)
                .totalSteps(steps)
                .distanceKm(distanceKm)
                .kcalBurned(totalCalories)
                .sleepDurationMinutes(totalSleepMinutes)
                .sleepQuality(totalSleepMinutes >= 420 ? "Good" : "Needs Improvement")
                .workoutCount(workoutCount)
                .workoutDurationMinutes(workoutDuration)
                .avgHeartRate(heartRates.isEmpty() ? 0 : (int) hrStats.getAverage())
                .maxHeartRate(heartRates.isEmpty() ? 0 : hrStats.getMax())
                .minHeartRate(heartRates.isEmpty() ? 0 : hrStats.getMin())
                .healthScore(score)
                .build();
    }

    private int calculateHealthScore(int steps, int sleepMinutes, int workoutCount) {
        int score = 0;
        score += Math.min(50, (steps / 10000.0) * 50);

        if (sleepMinutes >= 420 && sleepMinutes <= 540)
            score += 30;
        else if (sleepMinutes > 0)
            score += 15;

        if (workoutCount > 0)
            score += 20;

        return Math.min(100, score);
    }
}