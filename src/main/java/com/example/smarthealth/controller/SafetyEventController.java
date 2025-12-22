package com.example.smarthealth.controller;

import com.example.smarthealth.dto.safety.FallEventRequest;
import com.example.smarthealth.dto.safety.SosRequest;
import com.example.smarthealth.model.safety.FallEvent;
import com.example.smarthealth.model.safety.SosEvent;
import com.example.smarthealth.service.SafetyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/safety/events")
@RequiredArgsConstructor
public class SafetyEventController {

    private final SafetyService safetyService;

    @PostMapping("/fall")
    public ResponseEntity<FallEvent> logFallEvent(
            @RequestParam Long userId,
            @RequestBody FallEventRequest request) {
        return ResponseEntity.ok(safetyService.logFallEvent(userId, request));
    }

    @PostMapping("/sos")
    public ResponseEntity<SosEvent> triggerSos(
            @RequestParam Long userId,
            @RequestBody SosRequest request) {
        return ResponseEntity.ok(safetyService.triggerSos(userId, request));
    }
}