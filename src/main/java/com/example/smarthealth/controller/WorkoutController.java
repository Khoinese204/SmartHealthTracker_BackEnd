package com.example.smarthealth.controller;

import com.example.smarthealth.dto.health.WorkoutRequest;
import com.example.smarthealth.model.health.WorkoutSession;
import com.example.smarthealth.service.WorkoutService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/health/workouts")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutService workoutService;

    @PostMapping
    public ResponseEntity<WorkoutSession> saveWorkout(
            @RequestBody WorkoutRequest request) {

        WorkoutSession savedSession = workoutService.saveWorkout(request);
        return ResponseEntity.ok(savedSession);
    }

    @GetMapping("/history")
    public ResponseEntity<List<WorkoutSession>> getWorkoutHistory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        return ResponseEntity.ok(workoutService.getWorkoutHistory(fromDate, toDate));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutSession> getWorkoutById(@PathVariable Long id) {
        return ResponseEntity.ok(workoutService.getWorkoutById(id));
    }
}