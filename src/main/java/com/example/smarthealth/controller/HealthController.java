package com.example.smarthealth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "System", description = "System health & diagnostics")
public class HealthController {

    @Operation(summary = "Public health check API", description = "Không yêu cầu token. Dùng để kiểm tra backend đang hoạt động.", tags = {
            "System" }, security = {}) // không cần token
    @GetMapping("/api/health")
    public String healthCheck() {
        return "Smart Health Tracker Backend is running!";
    }
}