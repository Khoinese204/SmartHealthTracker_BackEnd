package com.example.smarthealth.controller;

import com.example.smarthealth.dto.health.WorkoutRequest;
import com.example.smarthealth.model.health.WorkoutSession;
import com.example.smarthealth.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/health/workouts")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutService workoutService;

    @PostMapping
    public ResponseEntity<WorkoutSession> saveWorkout(
            @RequestParam Long userId,
            @RequestBody WorkoutRequest request) {
        
        WorkoutSession savedSession = workoutService.saveWorkout(userId, request);
        return ResponseEntity.ok(savedSession);
    }
}