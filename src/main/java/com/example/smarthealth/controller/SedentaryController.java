package com.example.smarthealth.controller;

import com.example.smarthealth.dto.health.SedentaryRequest;
import com.example.smarthealth.model.health.SedentaryLog;
import com.example.smarthealth.service.SedentaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/health/sedentary")
@RequiredArgsConstructor
public class SedentaryController {

    private final SedentaryService sedentaryService;

    @PostMapping
    public ResponseEntity<SedentaryLog> logSedentary(
            @RequestParam Long userId,
            @RequestBody SedentaryRequest request) {
        return ResponseEntity.ok(sedentaryService.logSedentaryEvent(userId, request));
    }
}