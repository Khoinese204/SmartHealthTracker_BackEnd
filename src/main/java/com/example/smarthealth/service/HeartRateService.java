package com.example.smarthealth.service;

import com.example.smarthealth.config.CurrentUserService;
import com.example.smarthealth.dto.health.HeartRateRequest;
import com.example.smarthealth.model.auth.User;
import com.example.smarthealth.model.health.HeartRateRecord;
import com.example.smarthealth.repository.HeartRateRecordRepository;
import com.example.smarthealth.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HeartRateService {

    private final HeartRateRecordRepository heartRateRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public HeartRateRecord saveHeartRate(HeartRateRequest request) {
        User user = currentUserService.getCurrentUser();

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

    public List<HeartRateRecord> getHeartRateHistory(LocalDate fromDate, LocalDate toDate) {
        Long userId = currentUserService.getCurrentUser().getId();

        LocalDateTime start = fromDate.atStartOfDay();
        LocalDateTime end = toDate.atTime(LocalTime.MAX);

        return heartRateRepository.findAllByUserIdAndMeasuredAtBetweenOrderByMeasuredAtDesc(userId, start, end);
    }
}