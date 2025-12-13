package com.example.smarthealth.controller;

import com.example.smarthealth.model.auth.User;
import com.example.smarthealth.repository.UserRepository;
import com.example.smarthealth.service.HealthExportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class HealthExportController {

    private final HealthExportService healthExportService;
    private final UserRepository userRepository;

    // USER export chính mình
    @Operation(summary = "Export my health data as CSV", description = "USER export their own health data", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/api/export/health.csv")
    public ResponseEntity<Resource> exportMyHealthCsv(
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {
        Long userId = getCurrentUserId();
        String csv = healthExportService.exportCsvForUser(userId, from, to);

        return buildCsvResponse(csv, "health_export_user_" + userId + ".csv");
    }

    // ADMIN export theo userId
    @Operation(summary = "Export a user's health data as CSV", description = "ADMIN export health data for any user by their userId", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/api/admin/export/users/{userId}/health.csv")
    public ResponseEntity<Resource> exportUserHealthCsvByAdmin(
            @PathVariable Long userId,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {
        String csv = healthExportService.exportCsvForUser(userId, from, to);
        return buildCsvResponse(csv, "health_export_user_" + userId + ".csv");
    }

    private ResponseEntity<Resource> buildCsvResponse(String csv, String filename) {
        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(bytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .contentLength(bytes.length)
                .body(resource);
    }

    private Long getCurrentUserId() {
        // principal của bạn đang set email trong FirebaseAuthenticationFilter
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Current user not found in DB"));
        return user.getId();
    }
}
