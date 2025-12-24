package com.example.smarthealth.controller;

import com.example.smarthealth.dto.health.HeartRateRequest;
import com.example.smarthealth.model.health.HeartRateRecord;
import com.example.smarthealth.service.HeartRateService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/health/heart-rate")
@RequiredArgsConstructor
public class HeartRateController {

    private final HeartRateService heartRateService;

    @PostMapping
    public ResponseEntity<HeartRateRecord> saveHeartRate(
            @RequestBody HeartRateRequest request) {
        return ResponseEntity.ok(heartRateService.saveHeartRate(request));
    }

    @GetMapping("/history")
    public ResponseEntity<List<HeartRateRecord>> getHeartRateHistory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        return ResponseEntity.ok(heartRateService.getHeartRateHistory(fromDate, toDate));
    }
}