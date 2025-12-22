package com.example.smarthealth.controller;

import com.example.smarthealth.dto.health.HeartRateStatsResponse;
import com.example.smarthealth.dto.health.SleepStatsResponse;
import com.example.smarthealth.dto.health.WorkoutStatsResponse;
import com.example.smarthealth.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/workout")
    public ResponseEntity<WorkoutStatsResponse> getWorkoutStats(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return ResponseEntity.ok(statisticsService.getWorkoutStats(userId, fromDate, toDate));
    }

    @GetMapping("/sleep")
    public ResponseEntity<SleepStatsResponse> getSleepStats(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return ResponseEntity.ok(statisticsService.getSleepStats(userId, fromDate, toDate));
    }

    @GetMapping("/heart-rate")
    public ResponseEntity<HeartRateStatsResponse> getHeartRateStats(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return ResponseEntity.ok(statisticsService.getHeartRateStats(userId, fromDate, toDate));
    }

    @GetMapping("/workout/range")
    public ResponseEntity<WorkoutStatsResponse> getWorkoutStatsRange(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(statisticsService.getWorkoutStatsSpecific(userId, from, to));
    }

    // 5. Sleep theo giờ
    @GetMapping("/sleep/range")
    public ResponseEntity<SleepStatsResponse> getSleepStatsRange(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(statisticsService.getSleepStatsSpecific(userId, from, to));
    }

    // 6. Heart Rate theo giờ
    @GetMapping("/heart-rate/range")
    public ResponseEntity<HeartRateStatsResponse> getHeartRateStatsRange(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(statisticsService.getHeartRateStatsSpecific(userId, from, to));
    }
}