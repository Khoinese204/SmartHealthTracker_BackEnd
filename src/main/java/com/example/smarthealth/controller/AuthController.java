package com.example.smarthealth.controller;

import com.example.smarthealth.dto.auth.UserProfileDto;
import com.example.smarthealth.model.auth.User;
import com.example.smarthealth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

        private final UserRepository userRepository;

        @GetMapping("/me")
        public ResponseEntity<UserProfileDto> me() {
                String email = (String) SecurityContextHolder.getContext()
                                .getAuthentication().getPrincipal();

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalStateException("User not found in DB"));

                UserProfileDto dto = UserProfileDto.builder()
                                .id(user.getId())
                                .email(user.getEmail())
                                .fullName(user.getFullName())
                                .avatarUrl(user.getAvatarUrl())
                                .gender(user.getGender())
                                .heightCm(user.getHeightCm())
                                .weightKg(user.getWeightKg())
                                .role(user.getRole().getName())
                                .build();

                return ResponseEntity.ok(dto);
        }

}
