package com.example.smarthealth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.smarthealth.model.gamification.AchievementDefinition;

public interface AchievementDefinitionRepository extends JpaRepository<AchievementDefinition, Long> {
}