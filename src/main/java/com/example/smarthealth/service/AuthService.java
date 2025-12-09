package com.example.smarthealth.service;

import com.example.smarthealth.dto.auth.UserProfileDto;
import com.example.smarthealth.dto.auth.UserProfileUpdateRequest;
import com.example.smarthealth.model.auth.User;
import com.example.smarthealth.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

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
                .weightKg(user.getWeightKg() != null ? user.getWeightKg().doubleValue() : null)
                .role(user.getRole().getName())
                .build();
    }

    public User getCurrentUserEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthenticated");
        }

        String email = (String) authentication.getPrincipal();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found: " + email));
    }

    public UserProfileDto updateProfile(UserProfileUpdateRequest req) {
        User user = getCurrentUserEntity();

        if (req.getFullName() != null)
            user.setFullName(req.getFullName());
        if (req.getGender() != null)
            user.setGender(req.getGender());
        if (req.getHeightCm() != null)
            user.setHeightCm(req.getHeightCm());
        if (req.getWeightKg() != null) {
            user.setWeightKg(java.math.BigDecimal.valueOf(req.getWeightKg()));
        }
        if (req.getDateOfBirth() != null)
            user.setDateOfBirth(req.getDateOfBirth());

        userRepository.save(user);

        return toDto(user);
    }

    public UserProfileDto updateAvatar(MultipartFile file) throws IOException {
        User user = getCurrentUserEntity();

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Avatar file is empty");
        }

        // Demo: save file to local folder /uploads/avatars/
        String fileName = "avatar_" + user.getId() + "_" + System.currentTimeMillis() + ".jpg";
        Path uploadPath = Paths.get("uploads/avatars");
        Files.createDirectories(uploadPath);

        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, file.getBytes());

        // Set avatar URL (ở FE bạn sẽ tự map đường dẫn tĩnh)
        user.setAvatarUrl("/static/avatars/" + fileName);

        userRepository.save(user);

        return toDto(user);
    }

    private UserProfileDto toDto(User user) {
        return UserProfileDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .gender(user.getGender())
                .heightCm(user.getHeightCm())
                .weightKg(user.getWeightKg() != null ? user.getWeightKg().doubleValue() : null)
                .dateOfBirth(user.getDateOfBirth())
                .role(user.getRole().getName())
                .build();
    }

}
