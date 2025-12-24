package com.example.smarthealth.service;

import com.example.smarthealth.config.CurrentUserService;
import com.example.smarthealth.dto.health.WorkoutRequest;
import com.example.smarthealth.enums.WorkoutType; // Import Enum của bạn
import com.example.smarthealth.model.auth.User;
import com.example.smarthealth.model.health.WorkoutGpsPoint;
import com.example.smarthealth.model.health.WorkoutSession;
import com.example.smarthealth.repository.UserRepository;
import com.example.smarthealth.repository.WorkoutSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutService {

    private final WorkoutSessionRepository sessionRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public WorkoutSession saveWorkout(WorkoutRequest request) {
        User user = currentUserService.getCurrentUser();

        WorkoutSession session = WorkoutSession.builder()
                .user(user)
                .type(WorkoutType.valueOf(request.getType()))
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .durationSeconds(request.getDurationSeconds())
                .distanceMeters(request.getDistanceMeters())
                .avgSpeedMps(request.getAvgSpeedMps())
                .avgPaceSecPerKm(request.getAvgPaceSecPerKm())
                .calories(request.getCalories())
                .build();

        if (request.getGpsPoints() != null && !request.getGpsPoints().isEmpty()) {
            List<WorkoutGpsPoint> points = request.getGpsPoints().stream().map(dto -> WorkoutGpsPoint.builder()
                    .workoutSession(session)
                    .sequenceIndex(dto.getSequenceIndex())
                    .latitude(dto.getLatitude())
                    .longitude(dto.getLongitude())
                    .altitude(dto.getAltitude())
                    .timestamp(dto.getTimestamp())
                    .build()).collect(Collectors.toList());

            session.setGpsPoints(points);
        } else {
            session.setGpsPoints(new ArrayList<>());
        }

        return sessionRepository.save(session);
    }

    public List<WorkoutSession> getWorkoutHistory(LocalDate fromDate, LocalDate toDate) {
        Long userId = currentUserService.getCurrentUser().getId();

        LocalDateTime start = fromDate.atStartOfDay(); 
        LocalDateTime end = toDate.atTime(LocalTime.MAX);

        return sessionRepository.findAllByUserIdAndStartTimeBetweenOrderByStartTimeDesc(userId, start, end);
    }

    // ... code cũ ...

    public WorkoutSession getWorkoutById(Long id) {
        WorkoutSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workout not found"));

        Long currentUserId = currentUserService.getCurrentUser().getId();
        
        if (!session.getUser().getId().equals(currentUserId)) {
            throw new RuntimeException("Access denied: You do not own this workout");
        }

        return session;
    }
}