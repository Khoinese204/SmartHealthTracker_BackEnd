package com.example.smarthealth.model.gamification;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "achievement_definitions")
@Getter
@Setter
public class AchievementDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "rule_type", length = 50)
    private String ruleType;

    @Column(name = "rule_config", columnDefinition = "text")
    private String ruleConfig; // JSON string

    @Column(name = "icon_url", length = 500)
    private String iconUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}