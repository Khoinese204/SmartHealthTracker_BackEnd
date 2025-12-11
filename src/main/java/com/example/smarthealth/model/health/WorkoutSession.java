package com.example.smarthealth.model.health;

import com.example.smarthealth.enums.WorkoutType;
import com.example.smarthealth.model.auth.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "workout_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class WorkoutSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private WorkoutType type;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "distance_meters", precision = 10, scale = 2)
    private BigDecimal distanceMeters;

    @Column(name = "avg_speed_mps", precision = 10, scale = 3)
    private BigDecimal avgSpeedMps;

    @Column(name = "avg_pace_sec_per_km")
    private Integer avgPaceSecPerKm;

    private Integer calories;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "workoutSession", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkoutGpsPoint> gpsPoints;
}