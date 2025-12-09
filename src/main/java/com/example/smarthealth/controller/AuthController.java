package com.example.smarthealth.controller;

import com.example.smarthealth.dto.auth.UserProfileDto;
import com.example.smarthealth.service.AuthService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
