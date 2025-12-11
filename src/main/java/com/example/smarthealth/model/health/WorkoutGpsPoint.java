package com.example.smarthealth.model.health;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "workout_gps_points", indexes = {
    @Index(name = "idx_workout_gps_workout_id", columnList = "workout_id") 
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutGpsPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private WorkoutSession workoutSession;

    @Column(name = "sequence_index", nullable = false)
    private Integer sequenceIndex;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(precision = 8, scale = 2)
    private BigDecimal altitude;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}