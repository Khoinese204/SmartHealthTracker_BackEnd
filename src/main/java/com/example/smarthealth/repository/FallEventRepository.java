package com.example.smarthealth.repository;

import com.example.smarthealth.model.safety.FallEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FallEventRepository extends JpaRepository<FallEvent, Long> {
}