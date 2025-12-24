package com.example.smarthealth.service;

import com.example.smarthealth.config.CurrentUserService;
import com.example.smarthealth.dto.health.SleepRequest;
import com.example.smarthealth.model.auth.User;
import com.example.smarthealth.model.health.SleepSession;
import com.example.smarthealth.repository.SleepSessionRepository;
import com.example.smarthealth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SleepService {

    private final SleepSessionRepository sleepRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public SleepSession logSleepSession(SleepRequest request) {
        User user = currentUserService.getCurrentUser();

        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new IllegalArgumentException("Start time and End time must not be null");
        }

        long minutes = Duration.between(request.getStartTime(), request.getEndTime()).toMinutes();

        String quality = request.getQualityLevel();
        if (quality == null) {
            if (minutes >= 420)
                quality = "HIGH";
            else if (minutes >= 300)
                quality = "MEDIUM";
            else
                quality = "LOW";
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

    public List<SleepSession> getSleepHistory(LocalDate fromDate, LocalDate toDate) {
        Long userId = currentUserService.getCurrentUser().getId();

        LocalDateTime start = fromDate.atStartOfDay();
        LocalDateTime end = toDate.atTime(LocalTime.MAX);

        return sleepRepository.findAllByUserIdAndStartTimeBetweenOrderByStartTimeDesc(userId, start, end);
    }
}