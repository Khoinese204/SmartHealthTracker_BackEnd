package com.example.smarthealth.model.gamification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "challenge_participations", uniqueConstraints = @UniqueConstraint(columnNames = { "challenge_id",
        "user_id" }))
@Getter
@Setter
public class ChallengeParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @Column(name = "current_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal currentValue;

    @Column(name = "completion_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal completionPercentage;

    @Column(name = "is_completed", nullable = false)
    private boolean completed;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}