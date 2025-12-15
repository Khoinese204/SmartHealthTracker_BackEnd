package com.example.smarthealth.repository;

import com.example.smarthealth.model.safety.SosEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SosEventRepository extends JpaRepository<SosEvent, Long> {
}