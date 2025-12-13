package com.example.smarthealth.service;

import com.example.smarthealth.dto.admin.AdminCreateUserRequest;
import com.example.smarthealth.dto.admin.AdminUserSummaryDto;
import com.example.smarthealth.dto.common.PagedResult;
import com.example.smarthealth.model.auth.Role;
import com.example.smarthealth.model.auth.User;
import com.example.smarthealth.repository.RoleRepository;
import com.example.smarthealth.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AdminUserSummaryDto createUser(AdminCreateUserRequest req) {
        String email = req.getEmail().trim().toLowerCase();

        // 1) check DB
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists in DB");
        }

        // 2) create user on Firebase
        UserRecord fbUser;
        try {
            UserRecord.CreateRequest createReq = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(req.getPassword())
                    .setEmailVerified(false)
                    .setDisabled(false);

            if (req.getFullName() != null && !req.getFullName().isBlank()) {
                createReq.setDisplayName(req.getFullName().trim());
            }
            if (req.getAvatarUrl() != null && !req.getAvatarUrl().isBlank()) {
                createReq.setPhotoUrl(req.getAvatarUrl().trim());
            }

            fbUser = FirebaseAuth.getInstance().createUser(createReq);

        } catch (Exception e) {
            // Firebase tạo không được → fail luôn, DB không lưu
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot create Firebase user: " + e.getMessage());
        }

        // 3) role
        String roleName = (req.getRole() == null || req.getRole().isBlank())
                ? "USER"
                : req.getRole().trim().toUpperCase();

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found: " + roleName));

        // 4) save DB with firebase uid
        User user = User.builder()
                .email(email)
                .fullName(req.getFullName())
                .avatarUrl(req.getAvatarUrl())
                .firebaseUid(fbUser.getUid()) // ✅ UID lấy từ Firebase
                .authProvider("FIREBASE")
                .role(role)
                .isActive(req.getIsActive() != null ? req.getIsActive() : true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        return AdminUserSummaryDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole().getName())
                .active(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public AdminUserSummaryDto deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with id=" + userId));

        user.setIsActive(false);
        user.setUpdatedAt(LocalDateTime.now()); // nếu entity bạn có field updatedAt
        userRepository.save(user);

        return toAdminDto(user);
    }

    public AdminUserSummaryDto activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with id=" + userId));

        user.setIsActive(true);
        user.setUpdatedAt(LocalDateTime.now()); // nếu entity bạn có field updatedAt
        userRepository.save(user);

        return toAdminDto(user);
    }

    private AdminUserSummaryDto toAdminDto(User u) {
        return AdminUserSummaryDto.builder()
                .id(u.getId())
                .email(u.getEmail())
                .fullName(u.getFullName())
                .avatarUrl(u.getAvatarUrl())
                .role(u.getRole().getName())
                .active(u.getIsActive()) // vì bạn giữ field isActive
                .createdAt(u.getCreatedAt())
                .build();
    }

    public PagedResult<AdminUserSummaryDto> listUsers(
            int page, int size, String q, String role, Boolean active) {
        Pageable pageable = PageRequest.of(
                page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<User> users;

        if (q != null && !q.isBlank()) {
            users = userRepository.findByEmailContainingIgnoreCase(q.trim(), pageable);
        } else if (role != null && !role.isBlank()) {
            users = userRepository.findByRole_Name(role.toUpperCase(), pageable);
        } else if (active != null) {
            users = userRepository.findByIsActive(active, pageable);
        } else {
            users = userRepository.findAll(pageable);
        }

        Page<AdminUserSummaryDto> mapped = users.map(u -> AdminUserSummaryDto.builder()
                .id(u.getId())
                .email(u.getEmail())
                .fullName(u.getFullName())
                .avatarUrl(u.getAvatarUrl())
                .role(u.getRole().getName())
                .active(u.getIsActive())
                .createdAt(u.getCreatedAt())
                .build());

        return PagedResult.fromPage(mapped);
    }
}