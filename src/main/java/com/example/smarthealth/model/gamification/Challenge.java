package com.example.smarthealth.model.gamification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "challenges")
@Getter
@Setter
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "metric_type", nullable = false, length = 50)
    private String metricType; // STEPS | WORKOUT_COUNT

    @Column(name = "target_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal targetValue;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "is_global", nullable = false)
    private boolean global;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}