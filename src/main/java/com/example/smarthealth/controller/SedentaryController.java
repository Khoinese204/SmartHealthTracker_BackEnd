package com.example.smarthealth.controller;

import com.example.smarthealth.dto.health.SedentaryRequest;
import com.example.smarthealth.model.health.SedentaryLog;
import com.example.smarthealth.service.SedentaryService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/health/sedentary")
@RequiredArgsConstructor
public class SedentaryController {

    private final SedentaryService sedentaryService;

    @PostMapping
    public ResponseEntity<SedentaryLog> logSedentary(
            @RequestBody SedentaryRequest request) {
        return ResponseEntity.ok(sedentaryService.logSedentaryEvent(request));
    }

    @GetMapping("/history")
    public ResponseEntity<List<SedentaryLog>> getSedentaryHistory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        return ResponseEntity.ok(sedentaryService.getSedentaryHistory(fromDate, toDate));
    }
}