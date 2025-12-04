package com.example.smarthealth.repository;

import com.example.smarthealth.model.auth.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    Optional<DeviceToken> findByDeviceToken(String deviceToken);
}
