package com.example.smarthealth.controller;

import com.example.smarthealth.dto.health.SleepRequest;
import com.example.smarthealth.model.health.SleepSession;
import com.example.smarthealth.service.SleepService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/health/sleep")
@RequiredArgsConstructor
public class SleepController {

    private final SleepService sleepService;

    @PostMapping
    public ResponseEntity<SleepSession> logSleep(
            @RequestParam Long userId,
            @RequestBody SleepRequest request) {
        return ResponseEntity.ok(sleepService.logSleepSession(userId, request));
    }
}