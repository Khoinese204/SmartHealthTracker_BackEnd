package com.example.smarthealth.model.gamification;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_achievements", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "achievement_id" }))
@Getter
@Setter
public class UserAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "achievement_id", nullable = false)
    private AchievementDefinition achievement;

    @Column(name = "unlocked_at", nullable = false)
    private LocalDateTime unlockedAt;
}