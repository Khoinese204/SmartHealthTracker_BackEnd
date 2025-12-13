package com.example.smarthealth.service;


import com.example.smarthealth.model.health.*;
import com.example.smarthealth.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HealthExportService {

    private final StepDailyRepository stepDailyRepository;
    private final WorkoutRepository workoutRepository;
    private final SleepSessionRepository sleepSessionRepository;
    private final HeartRateRecordRepository heartRateRepository;
    private final SedentaryLogRepository sedentaryLogRepository;

    public String exportCsvForUser(Long userId, LocalDate from, LocalDate to) {
        // guard
        if (from == null) from = LocalDate.now().minusDays(7);
        if (to == null) to = LocalDate.now();
        if (from.isAfter(to)) {
            // swap đơn giản
            LocalDate tmp = from;
            from = to;
            to = tmp;
        }

        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt = to.atTime(LocalTime.MAX);

        List<StepDaily> steps = stepDailyRepository.findByUserIdAndDateBetween(userId, from, to);
        List<WorkoutSession> workouts = workoutRepository.findByUserIdAndStartDateBetween(userId, from, to);
        List<SleepSession> sleeps = sleepSessionRepository.findByUserIdAndStartTimeBetween(userId, fromDt, toDt);
        List<HeartRateRecord> hrs = heartRateRepository.findByUserIdAndMeasuredAtBetween(userId, fromDt, toDt);
        List<SedentaryLog> sedentaries = sedentaryLogRepository.findByUserIdAndStartTimeBetween(userId, fromDt, toDt);

        StringBuilder sb = new StringBuilder();

        // (optional) BOM để Excel đọc UTF-8 chuẩn
        sb.append('\uFEFF');

        // Header (1 file, nhiều loại data)
        sb.append("timestamp,type,metric,value,unit,details").append("\n");

        // Steps daily
        steps.stream()
                .sorted(Comparator.comparing(StepDaily::getDate))
                .forEach(s -> row(sb,
                        s.getDate().atStartOfDay().toString(),
                        "STEP_DAILY",
                        "total_steps",
                        String.valueOf(s.getTotalSteps()),
                        "steps",
                        ""
                ));

        // Workouts
        workouts.stream()
                .sorted(Comparator.comparing(WorkoutSession::getStartTime))
                .forEach(w -> row(sb,
                        safe(w.getStartTime()),
                        "WORKOUT",
                        "session",
                        "", // value để trống vì session nhiều field
                        "",
                        "type=" + safe(w.getType()) +
                                ";duration_seconds=" + safe(w.getDurationSeconds()) +
                                ";distance_meters=" + safe(w.getDistanceMeters()) +
                                ";calories=" + safe(w.getCalories()) +
                                ";start=" + safe(w.getStartTime()) +
                                ";end=" + safe(w.getEndTime())
                ));

        // Sleep
        sleeps.stream()
                .sorted(Comparator.comparing(SleepSession::getStartTime))
                .forEach(s -> row(sb,
                        safe(s.getStartTime()),
                        "SLEEP",
                        "duration_minutes",
                        safeNumber(s.getDurationMinutes()),
                        "minutes",
                        "quality=" + safe(s.getQualityLevel()) +
                                ";start=" + safe(s.getStartTime()) +
                                ";end=" + safe(s.getEndTime())
                ));

        // Heart rate
        hrs.stream()
                .sorted(Comparator.comparing(HeartRateRecord::getMeasuredAt))
                .forEach(h -> row(sb,
                        safe(h.getMeasuredAt()),
                        "HEART_RATE",
                        "bpm",
                        safeNumber(h.getBpm()),
                        "bpm",
                        "note=" + safe(h.getNote())
                ));

        // Sedentary
        sedentaries.stream()
                .sorted(Comparator.comparing(SedentaryLog::getStartTime))
                .forEach(s -> row(sb,
                        safe(s.getStartTime()),
                        "SEDENTARY",
                        "duration_minutes",
                        safeNumber(s.getDurationMinutes()),
                        "minutes",
                        "steps_in_window=" + safe(s.getStepsInWindow()) +
                                ";start=" + safe(s.getStartTime()) +
                                ";end=" + safe(s.getEndTime())
                ));

        return sb.toString();
    }

    private void row(StringBuilder sb, String... cols) {
        for (int i = 0; i < cols.length; i++) {
            sb.append(csv(cols[i]));
            if (i < cols.length - 1) sb.append(',');
        }
        sb.append('\n');
    }

    // CSV escape: wrap "..." nếu có comma/quote/newline
    private String csv(String v) {
        if (v == null) v = "";
        boolean needQuote = v.contains(",") || v.contains("\"") || v.contains("\n") || v.contains("\r");
        if (needQuote) {
            v = v.replace("\"", "\"\"");
            return "\"" + v + "\"";
        }
        return v;
    }

    private String safe(Object o) {
        return o == null ? "" : String.valueOf(o);
    }

    private String safeNumber(Object o) {
        return o == null ? "0" : String.valueOf(o);
    }
}

