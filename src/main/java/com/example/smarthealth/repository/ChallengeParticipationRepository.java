package com.example.smarthealth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.smarthealth.model.gamification.ChallengeParticipation;

public interface ChallengeParticipationRepository extends JpaRepository<ChallengeParticipation, Long> {

    Optional<ChallengeParticipation> findByChallenge_IdAndUserId(Long challengeId, Long userId);

    List<ChallengeParticipation> findByUserIdOrderByJoinedAtDesc(Long userId);
}