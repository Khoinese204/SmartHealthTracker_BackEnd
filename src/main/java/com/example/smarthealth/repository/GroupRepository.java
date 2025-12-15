package com.example.smarthealth.repository;

import com.example.smarthealth.model.social.SocialGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<SocialGroup, Long> {
}