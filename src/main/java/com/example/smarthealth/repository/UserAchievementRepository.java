package com.example.smarthealth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.smarthealth.model.gamification.UserAchievement;

public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {

    boolean existsByUserIdAndAchievement_Id(Long userId, Long achievementId);

    List<UserAchievement> findByUserIdOrderByUnlockedAtDesc(Long userId);
}