package com.example.smarthealth.service;

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

    public WorkoutStatsResponse getWorkoutStats(Long userId, LocalDate fromDate, LocalDate toDate) {
        return workoutRepository.getStats(
                userId,
                fromDate.atStartOfDay(),
                toDate.atTime(LocalTime.MAX));
    }

    public SleepStatsResponse getSleepStats(Long userId, LocalDate fromDate, LocalDate toDate) {
        return sleepRepository.getStats(
                userId,
                fromDate.atStartOfDay(),
                toDate.atTime(LocalTime.MAX));
    }

    public HeartRateStatsResponse getHeartRateStats(Long userId, LocalDate fromDate, LocalDate toDate) {
        return heartRateRepository.getStats(
                userId,
                fromDate.atStartOfDay(),
                toDate.atTime(LocalTime.MAX));
    }

    public WorkoutStatsResponse getWorkoutStatsSpecific(Long userId, LocalDateTime from, LocalDateTime to) {
        return workoutRepository.getStats(userId, from, to);
    }

    public SleepStatsResponse getSleepStatsSpecific(Long userId, LocalDateTime from, LocalDateTime to) {
        return sleepRepository.getStats(userId, from, to);
    }

    public HeartRateStatsResponse getHeartRateStatsSpecific(Long userId, LocalDateTime from, LocalDateTime to) {
        return heartRateRepository.getStats(userId, from, to);
    }
}