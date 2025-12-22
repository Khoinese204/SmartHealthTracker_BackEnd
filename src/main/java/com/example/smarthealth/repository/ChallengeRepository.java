package com.example.smarthealth.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.smarthealth.model.gamification.Challenge;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    List<Challenge> findByEndDateGreaterThanEqual(LocalDate today);

    Optional<Challenge> findByCode(String code);
}