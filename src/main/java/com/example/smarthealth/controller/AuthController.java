package com.example.smarthealth.controller;

import com.example.smarthealth.dto.auth.UserProfileDto;
import com.example.smarthealth.dto.auth.UserProfileUpdateRequest;
import com.example.smarthealth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication & User Profile APIs")
public class AuthController {

        private final AuthService authService;

        @Operation(summary = "Get current logged-in user profile", description = """
                        Trả về thông tin hồ sơ của người dùng hiện tại thông qua Firebase Token.
                        API yêu cầu Bearer Token hợp lệ.
                        """, tags = { "Authentication" }, security = @SecurityRequirement(name = "bearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "User profile returned successfully", content = @Content(schema = @Schema(implementation = UserProfileDto.class))),
                        @ApiResponse(responseCode = "401", description = "Invalid or missing token")
        })
        @GetMapping("/me")
        public ResponseEntity<UserProfileDto> me() {
                UserProfileDto dto = authService.getCurrentUserProfile();
                return ResponseEntity.ok(dto);
        }

        @Operation(summary = "Update user profile", description = "Cho phép cập nhật họ tên, ngày sinh, giới tính, địa chỉ...", tags = {
                        "Authentication" }, security = @SecurityRequirement(name = "bearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Profile updated successfully", content = @Content(schema = @Schema(implementation = UserProfileDto.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid request payload"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized")
        })
        @PutMapping("/me")
        public ResponseEntity<UserProfileDto> updateProfile(
                        @RequestBody UserProfileUpdateRequest req) {

                return ResponseEntity.ok(authService.updateProfile(req));
        }

        @Operation(summary = "Upload new avatar", description = """
                        Upload avatar mới cho user.
                        File phải là dạng JPEG/PNG, size < 5MB.
                        """, tags = { "Authentication" }, security = @SecurityRequirement(name = "bearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Avatar updated successfully", content = @Content(schema = @Schema(implementation = UserProfileDto.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid file upload"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized")
        })
        @PostMapping("/me/avatar")
        public ResponseEntity<UserProfileDto> updateAvatar(
                        @RequestParam("file") MultipartFile file) throws IOException {

                return ResponseEntity.ok(authService.updateAvatar(file));
        }

}
