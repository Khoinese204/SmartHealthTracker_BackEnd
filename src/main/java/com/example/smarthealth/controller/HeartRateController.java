package com.example.smarthealth.controller;

import com.example.smarthealth.dto.health.HeartRateRequest;
import com.example.smarthealth.model.health.HeartRateRecord;
import com.example.smarthealth.service.HeartRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/health/heart-rate")
@RequiredArgsConstructor
public class HeartRateController {

    private final HeartRateService heartRateService;

    @PostMapping
    public ResponseEntity<HeartRateRecord> saveHeartRate(
            @RequestParam Long userId,
            @RequestBody HeartRateRequest request) {
        return ResponseEntity.ok(heartRateService.saveHeartRate(userId, request));
    }
}