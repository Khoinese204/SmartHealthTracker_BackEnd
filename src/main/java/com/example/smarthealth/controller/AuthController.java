package com.example.smarthealth.controller;

import com.example.smarthealth.dto.auth.UserProfileDto;
import com.example.smarthealth.dto.auth.UserProfileUpdateRequest;
import com.example.smarthealth.service.AuthService;

import lombok.RequiredArgsConstructor;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

        private final AuthService authService;

        @GetMapping("/me")
        public ResponseEntity<UserProfileDto> me() {
                UserProfileDto dto = authService.getCurrentUserProfile();
                return ResponseEntity.ok(dto);
        }

        @PutMapping("/me")
        public ResponseEntity<UserProfileDto> updateProfile(
                        @RequestBody UserProfileUpdateRequest req) {

                return ResponseEntity.ok(authService.updateProfile(req));
        }

        @PostMapping("/me/avatar")
        public ResponseEntity<UserProfileDto> updateAvatar(
                        @RequestParam("file") MultipartFile file) throws IOException {

                return ResponseEntity.ok(authService.updateAvatar(file));
        }

}
