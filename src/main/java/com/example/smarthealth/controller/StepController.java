package com.example.smarthealth.controller;

import com.example.smarthealth.dto.health.StepResponse;
import com.example.smarthealth.dto.health.StepSyncRequest;
import com.example.smarthealth.service.StepService;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
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
            @RequestBody StepSyncRequest request) {
        return ResponseEntity.ok(stepService.syncSteps(request));
    }

    @GetMapping("/today")
    public ResponseEntity<StepResponse> getTodaySteps() {
        return ResponseEntity.ok(stepService.getStepsByDate(LocalDate.now()));
    }

    @GetMapping
    public ResponseEntity<StepResponse> getStepsByDate(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        LocalDate targetDate = date != null ? date : LocalDate.now();
        return ResponseEntity.ok(stepService.getStepsByDate(targetDate));
    }
}