package com.example.smarthealth.controller;

import com.example.smarthealth.dto.admin.AdminCreateUserRequest;
import com.example.smarthealth.dto.admin.AdminUserSummaryDto;
import com.example.smarthealth.dto.common.ApiSuccess;
import com.example.smarthealth.dto.common.PagedResult;
import com.example.smarthealth.service.AdminUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @Operation(summary = "Add a new user")
    @PostMapping
    public ResponseEntity<ApiSuccess<AdminUserSummaryDto>> create(@Valid @RequestBody AdminCreateUserRequest req) {
        AdminUserSummaryDto data = adminUserService.createUser(req);
        return ResponseEntity.ok(ApiSuccess.success("User created successfully", data));
    }

    @Operation(summary = "Admin – List users")
    @GetMapping("/users")
    public ResponseEntity<ApiSuccess<PagedResult<AdminUserSummaryDto>>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean active) {
        var data = adminUserService.listUsers(page, size, q, role, active);
        return ResponseEntity.ok(
                ApiSuccess.success("Users fetched successfully", data));
    }

    @Operation(summary = "Deactivate a user")
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ApiSuccess<AdminUserSummaryDto>> deactivate(@PathVariable("id") Long id) {
        AdminUserSummaryDto data = adminUserService.deactivateUser(id);
        return ResponseEntity.ok(ApiSuccess.success("User deactivated successfully", data));
    }

    @Operation(summary = "Activate a user")
    @PutMapping("/{id}/activate")
    public ResponseEntity<ApiSuccess<AdminUserSummaryDto>> activate(@PathVariable("id") Long id) {
        AdminUserSummaryDto data = adminUserService.activateUser(id);
        return ResponseEntity.ok(ApiSuccess.success("User activated successfully", data));
    }

}
