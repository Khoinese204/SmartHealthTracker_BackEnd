package com.example.smarthealth.service;

import com.example.smarthealth.dto.health.SleepRequest;
import com.example.smarthealth.model.auth.User;
import com.example.smarthealth.model.health.SleepSession;
import com.example.smarthealth.repository.SleepSessionRepository;
import com.example.smarthealth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class SleepService {

    private final SleepSessionRepository sleepRepository;
    private final UserRepository userRepository;

    @Transactional
    public SleepSession logSleepSession(Long userId, SleepRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new IllegalArgumentException("Start time and End time must not be null");
        }

        long minutes = Duration.between(request.getStartTime(), request.getEndTime()).toMinutes();

        String quality = request.getQualityLevel();
        if (quality == null) {
            if (minutes >= 420) quality = "HIGH";
            else if (minutes >= 300) quality = "MEDIUM";
            else quality = "LOW";
        }

        SleepSession session = SleepSession.builder()
                .user(user)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .durationMinutes((int) minutes)
                .qualityLevel(quality)
                .build();

        return sleepRepository.save(session);
    }
}