package com.example.smarthealth.controller;

import com.example.smarthealth.dto.auth.UpdateAvatarRequest;
import com.example.smarthealth.dto.auth.UserProfileDto;
import com.example.smarthealth.dto.auth.UserProfileResponse;
import com.example.smarthealth.dto.auth.UserProfileUpdateRequest;
import com.example.smarthealth.dto.common.ApiError;
import com.example.smarthealth.dto.common.ApiSuccess;
import com.example.smarthealth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication & User Profile APIs")
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "Get current user profile", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "User profile returned successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfileResponse.class))),
      @ApiResponse(responseCode = "401", description = "Missing or invalid token", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class), examples = {
          @ExampleObject(value = """
              {
                "status": 401,
                "message": "Unauthenticated"
              }
              """)
      })),
      @ApiResponse(responseCode = "403", description = "Not enough permission", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class), examples = {
          @ExampleObject(value = """
              {
                "status": 403,
                "message": "Forbidden"
              }
              """)
      }))
  })
  @GetMapping("/me")
  public ResponseEntity<ApiSuccess<UserProfileDto>> me() {
    UserProfileDto data = authService.getCurrentUserProfile();
    return ResponseEntity.ok(
        ApiSuccess.success("User profile returned successfully", data));
  }

  @Operation(summary = "Update user profile", description = "Cho phép cập nhật họ tên, ngày sinh, giới tính, chiều cao, cân nặng...", tags = {
      "Authentication" }, security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "User profile updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfileResponse.class))),
      @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class), examples = {
          @ExampleObject(value = """
              {
                "status": 400,
                "message": "Bad request"
              }
              """)
      })),
      @ApiResponse(responseCode = "401", description = "Missing or invalid token", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class), examples = {
          @ExampleObject(value = """
              {
                "status": 401,
                "message": "Unauthenticated"
              }
              """)
      })),
      @ApiResponse(responseCode = "403", description = "Not enough permission", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class), examples = {
          @ExampleObject(value = """
              {
                "status": 403,
                "message": "Forbidden"
              }
              """)
      }))
  })
  @PutMapping("/me")
  public ResponseEntity<ApiSuccess<UserProfileDto>> updateProfile(
      @RequestBody UserProfileUpdateRequest req) {
    UserProfileDto data = authService.updateProfile(req);
    return ResponseEntity.ok(
        ApiSuccess.success("User profile updated successfully", data));
  }

  @PostMapping(value = "/me/avatar", consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Update avatar URL", description = """
      Frontend upload ảnh lên Cloudinary/Firebase Storage trước,
      sau đó gọi API này để lưu avatarUrl vào DB.
      """, security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Avatar updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfileResponse.class))),
      @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
      @ApiResponse(responseCode = "401", description = "Missing or invalid token", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
      @ApiResponse(responseCode = "403", description = "Not enough permission", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
  })
  public ResponseEntity<ApiSuccess<UserProfileDto>> updateAvatar(
      @RequestBody @jakarta.validation.Valid UpdateAvatarRequest req) {
    UserProfileDto data = authService.updateAvatarUrl(req.getAvatarUrl());
    return ResponseEntity.ok(ApiSuccess.success("Avatar updated successfully", data));
  }

}
