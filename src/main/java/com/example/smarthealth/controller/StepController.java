package com.example.smarthealth.controller;

import com.example.smarthealth.dto.health.StepResponse;
import com.example.smarthealth.dto.health.StepSyncRequest;
import com.example.smarthealth.service.StepService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/health/steps")
@RequiredArgsConstructor
public class StepController {

    private final StepService stepService;
    @PostMapping
    public ResponseEntity<StepResponse> syncSteps(
            @RequestParam Long userId,
            @RequestBody StepSyncRequest request) {
        return ResponseEntity.ok(stepService.syncSteps(userId, request));
    }

    @GetMapping("/today")
    public ResponseEntity<StepResponse> getTodaySteps(@RequestParam Long userId) {
        return ResponseEntity.ok(stepService.getStepsByDate(userId, LocalDate.now()));
    }
}