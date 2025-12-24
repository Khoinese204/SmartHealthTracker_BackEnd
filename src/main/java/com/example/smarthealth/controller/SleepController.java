package com.example.smarthealth.controller;

import com.example.smarthealth.dto.health.SleepRequest;
import com.example.smarthealth.model.health.SleepSession;
import com.example.smarthealth.service.SleepService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/health/sleep")
@RequiredArgsConstructor
public class SleepController {

    private final SleepService sleepService;

    @PostMapping
    public ResponseEntity<SleepSession> logSleep(
            @RequestBody SleepRequest request) {
        return ResponseEntity.ok(sleepService.logSleepSession(request));
    }

    @GetMapping("/history")
    public ResponseEntity<List<SleepSession>> getSleepHistory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        return ResponseEntity.ok(sleepService.getSleepHistory(fromDate, toDate));
    }
}