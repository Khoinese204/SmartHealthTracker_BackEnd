package com.example.smarthealth.controller;

import com.example.smarthealth.dto.auth.UserProfileDto;
import com.example.smarthealth.model.auth.User;
import com.example.smarthealth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

        private final UserRepository userRepository;

        @GetMapping("/me")
        public ResponseEntity<UserProfileDto> me() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                // Không có auth hoặc chỉ là anonymous → 401
                if (authentication == null
                                || !authentication.isAuthenticated()
                                || authentication instanceof AnonymousAuthenticationToken) {
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthenticated");
                }

                Object principal = authentication.getPrincipal();

                System.out.println(">>> principal class = " + principal.getClass());
                System.out.println(">>> principal value = " + principal);

                String email = (String) principal; // sau khi filter đã set principal = email

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalStateException("User not found in DB: " + email));

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
