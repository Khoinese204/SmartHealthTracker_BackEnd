package com.example.smarthealth.service;

import com.example.smarthealth.dto.auth.UserProfileDto;
import com.example.smarthealth.model.auth.User;
import com.example.smarthealth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public UserProfileDto getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthenticated");
        }

        Object principal = authentication.getPrincipal();

        System.out.println(">>> principal class = " + principal.getClass());
        System.out.println(">>> principal value = " + principal);

        String email = (String) principal; // đã được filter set = email

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found in DB: " + email));

        return UserProfileDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .gender(user.getGender())
                .heightCm(user.getHeightCm())
                .weightKg(user.getWeightKg())
                .role(user.getRole().getName())
                .build();
    }
}
