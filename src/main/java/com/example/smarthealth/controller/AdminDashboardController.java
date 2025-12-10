package com.example.smarthealth.controller;

import com.example.smarthealth.dto.dashboard.AdminDashboardResponse;
import com.example.smarthealth.dto.common.ApiSuccess;
import com.example.smarthealth.service.AdminDashboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    @Operation(
            summary = "Get admin dashboard summary",
            description = "API chỉ dành cho ADMIN. Yêu cầu Firebase token + role ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(
            responseCode = "200",
            description = "Success",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AdminDashboardResponse.class))
    )
    @ApiResponse(responseCode = "401", description = "Missing or invalid token")
    @ApiResponse(responseCode = "403", description = "Not authorized (ADMIN only)")
    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiSuccess<AdminDashboardResponse>> getDashboard() {

        AdminDashboardResponse data = dashboardService.getDashboard();

        return ResponseEntity.ok(
                ApiSuccess.success("Dashboard loaded successfully", data)
        );
    }
}
