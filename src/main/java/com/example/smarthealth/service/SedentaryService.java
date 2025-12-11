package com.example.smarthealth.service;

import com.example.smarthealth.dto.health.SedentaryRequest;
import com.example.smarthealth.model.auth.User;
import com.example.smarthealth.model.health.SedentaryLog;
import com.example.smarthealth.repository.SedentaryLogRepository;
import com.example.smarthealth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class SedentaryService {

    private final SedentaryLogRepository sedentaryLogRepository;
    private final UserRepository userRepository;

    private static final long SEDENTARY_THRESHOLD_MINUTES = 30;

    @Transactional
    public SedentaryLog logSedentaryEvent(Long userId, SedentaryRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        long durationMinutes = Duration.between(request.getStartTime(), request.getEndTime()).toMinutes();

        SedentaryLog log = SedentaryLog.builder()
                .user(user)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .durationMinutes((int) durationMinutes)
                .stepsInWindow(request.getStepsInWindow() != null ? request.getStepsInWindow() : 0)
                .build();

        SedentaryLog savedLog = sedentaryLogRepository.save(log);

        if (durationMinutes >= SEDENTARY_THRESHOLD_MINUTES) {
            sendAlertToUser(user.getFullName(), durationMinutes);
        }

        return savedLog;
    }

    private void sendAlertToUser(String userName, long minutes) {
        System.out.println("==========================================");
        System.out.println("🔔 [NOTIFICATION] Gửi tới: " + userName);
        System.out.println("🔔 Nội dung: Bạn đã ngồi yên " + minutes + " phút rồi! Hãy đứng dậy đi lại nhé!");
        System.out.println("==========================================");
    }
}