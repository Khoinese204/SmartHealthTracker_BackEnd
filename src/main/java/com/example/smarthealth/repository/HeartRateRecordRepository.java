package com.example.smarthealth.repository;

import com.example.smarthealth.model.health.HeartRateRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HeartRateRecordRepository extends JpaRepository<HeartRateRecord, Long> {
    List<HeartRateRecord> findByUserIdOrderByMeasuredAtDesc(Long userId);
}