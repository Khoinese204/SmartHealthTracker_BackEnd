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
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return ResponseEntity.ok(statisticsService.getWorkoutStats(fromDate, toDate));
    }

    @GetMapping("/sleep")
    public ResponseEntity<SleepStatsResponse> getSleepStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return ResponseEntity.ok(statisticsService.getSleepStats(fromDate, toDate));
    }

    @GetMapping("/heart-rate")
    public ResponseEntity<HeartRateStatsResponse> getHeartRateStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return ResponseEntity.ok(statisticsService.getHeartRateStats(fromDate, toDate));
    }

    @GetMapping("/workout/range")
    public ResponseEntity<WorkoutStatsResponse> getWorkoutStatsRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(statisticsService.getWorkoutStatsSpecific(from, to));
    }

    @GetMapping("/sleep/range")
    public ResponseEntity<SleepStatsResponse> getSleepStatsRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(statisticsService.getSleepStatsSpecific(from, to));
    }

    @GetMapping("/heart-rate/range")
    public ResponseEntity<HeartRateStatsResponse> getHeartRateStatsRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(statisticsService.getHeartRateStatsSpecific(from, to));
    }
}