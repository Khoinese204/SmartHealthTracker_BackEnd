package com.example.smarthealth.service;

import com.example.smarthealth.config.CurrentUserService;
import com.example.smarthealth.dto.health.HeartRateStatsResponse;
import com.example.smarthealth.dto.health.SleepStatsResponse;
import com.example.smarthealth.dto.health.WorkoutStatsResponse;
import com.example.smarthealth.repository.HeartRateRecordRepository;
import com.example.smarthealth.repository.SleepSessionRepository;
import com.example.smarthealth.repository.WorkoutSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final WorkoutSessionRepository workoutRepository;
    private final SleepSessionRepository sleepRepository;
    private final HeartRateRecordRepository heartRateRepository;
    private final CurrentUserService currentUserService;

    public WorkoutStatsResponse getWorkoutStats(LocalDate fromDate, LocalDate toDate) {
        Long userId = currentUserService.getCurrentUser().getId();
        return workoutRepository.getStats(
                userId,
                fromDate.atStartOfDay(),
                toDate.atTime(LocalTime.MAX));
    }

    public SleepStatsResponse getSleepStats(LocalDate fromDate, LocalDate toDate) {
        Long userId = currentUserService.getCurrentUser().getId();
        return sleepRepository.getStats(
                userId,
                fromDate.atStartOfDay(),
                toDate.atTime(LocalTime.MAX));
    }

    public HeartRateStatsResponse getHeartRateStats(LocalDate fromDate, LocalDate toDate) {
        Long userId = currentUserService.getCurrentUser().getId();
        return heartRateRepository.getStats(
                userId,
                fromDate.atStartOfDay(),
                toDate.atTime(LocalTime.MAX));
    }

    public WorkoutStatsResponse getWorkoutStatsSpecific(LocalDateTime from, LocalDateTime to) {
        Long userId = currentUserService.getCurrentUser().getId();
        return workoutRepository.getStats(userId, from, to);
    }

    public SleepStatsResponse getSleepStatsSpecific(LocalDateTime from, LocalDateTime to) {
        Long userId = currentUserService.getCurrentUser().getId();
        return sleepRepository.getStats(userId, from, to);
    }

    public HeartRateStatsResponse getHeartRateStatsSpecific(LocalDateTime from, LocalDateTime to) {
        Long userId = currentUserService.getCurrentUser().getId();
        return heartRateRepository.getStats(userId, from, to);
    }
}