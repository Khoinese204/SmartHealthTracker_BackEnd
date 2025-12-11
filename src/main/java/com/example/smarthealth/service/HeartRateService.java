package com.example.smarthealth.service;

import com.example.smarthealth.dto.health.HeartRateRequest;
import com.example.smarthealth.model.auth.User;
import com.example.smarthealth.model.health.HeartRateRecord;
import com.example.smarthealth.repository.HeartRateRecordRepository;
import com.example.smarthealth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HeartRateService {

    private final HeartRateRecordRepository heartRateRepository;
    private final UserRepository userRepository;

    @Transactional
    public HeartRateRecord saveHeartRate(Long userId, HeartRateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getBpm() == null || request.getBpm() < 30 || request.getBpm() > 250) {
            throw new IllegalArgumentException("Chỉ số nhịp tim không hợp lệ (phải từ 30 - 250 bpm)");
        }

        HeartRateRecord record = HeartRateRecord.builder()
                .user(user)
                .bpm(request.getBpm())
                .note(request.getNote())
                .measuredAt(request.getMeasuredAt())
                .build();

        return heartRateRepository.save(record);
    }
}