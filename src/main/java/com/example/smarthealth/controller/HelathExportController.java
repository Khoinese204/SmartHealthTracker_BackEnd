package com.example.smarthealth.controller;

import com.example.smarthealth.model.auth.User;
import com.example.smarthealth.repository.UserRepository;
import com.example.smarthealth.service.HealthExportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Export", description = "Export health data to CSV")
public class HelathExportController {

        private final HealthExportService healthExportService;
        private final UserRepository userRepository;

        @Operation(summary = "Export my health data as CSV", description = """
                        Export CSV cho user hiện tại (dữ liệu của chính mình) theo khoảng ngày.
                        Nếu không truyền from/to: mặc định 7 ngày gần nhất.
                        CSV trả về dạng file download (attachment).
                        """, security = @SecurityRequirement(name = "bearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "CSV file returned", content = @Content(mediaType = "text/csv", schema = @Schema(type = "string", format = "binary"))),
                        @ApiResponse(responseCode = "401", description = "Unauthenticated (missing/invalid token)"),
                        @ApiResponse(responseCode = "403", description = "Forbidden (account deactivated or no permission)"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @GetMapping("/api/export/health.csv")
        public ResponseEntity<Resource> exportMyHealthCsv(
                        @Parameter(in = ParameterIn.QUERY, description = "Start date (YYYY-MM-DD). Default: today-7", example = "2025-12-01") @RequestParam(required = false) LocalDate from,

                        @Parameter(in = ParameterIn.QUERY, description = "End date (YYYY-MM-DD). Default: today", example = "2025-12-13") @RequestParam(required = false) LocalDate to) {
                Long userId = getCurrentUserId();
                String csv = healthExportService.exportCsvForUser(userId, from, to);
                return buildCsvResponse(csv, "health_export_user_" + userId + ".csv");
        }

        @Operation(summary = "Admin export user's health data as CSV", description = """
                        Admin export CSV cho 1 user bất kỳ theo userId.
                        CSV trả về dạng file download (attachment).
                        """, security = @SecurityRequirement(name = "bearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "CSV file returned", content = @Content(mediaType = "text/csv", schema = @Schema(type = "string", format = "binary"))),
                        @ApiResponse(responseCode = "401", description = "Unauthenticated (missing/invalid token)"),
                        @ApiResponse(responseCode = "403", description = "Forbidden (ADMIN only)"),
                        @ApiResponse(responseCode = "404", description = "User not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @GetMapping("/api/admin/export/users/{userId}/health.csv")
        public ResponseEntity<Resource> exportUserHealthCsvByAdmin(
                        @Parameter(in = ParameterIn.PATH, description = "Target userId to export", example = "1") @PathVariable Long userId,

                        @Parameter(in = ParameterIn.QUERY, description = "Start date (YYYY-MM-DD). Default: today-7", example = "2025-12-01") @RequestParam(required = false) LocalDate from,

                        @Parameter(in = ParameterIn.QUERY, description = "End date (YYYY-MM-DD). Default: today", example = "2025-12-13") @RequestParam(required = false) LocalDate to) {
                // (optional) validate user exists để đúng 404 như docs
                if (userRepository.findById(userId).isEmpty()) {
                        return ResponseEntity.status(404).build();
                }

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
                // principal hiện tại bạn set là email trong FirebaseAuthenticationFilter
                String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalStateException("Current user not found in DB"));
                return user.getId();
        }
}
