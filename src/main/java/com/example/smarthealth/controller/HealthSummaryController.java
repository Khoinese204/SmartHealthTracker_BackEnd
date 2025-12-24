package com.example.smarthealth.controller;

import com.example.smarthealth.dto.health.DailySummaryDto;
import com.example.smarthealth.service.HealthSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/health/summary")
@RequiredArgsConstructor
public class HealthSummaryController {

    private final HealthSummaryService summaryService;

    @GetMapping
    public ResponseEntity<DailySummaryDto> getDailySummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate targetDate = (date != null) ? date : LocalDate.now();

        return ResponseEntity.ok(summaryService.getDailySummary(targetDate));
    }
}