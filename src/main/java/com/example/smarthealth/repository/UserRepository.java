package com.example.smarthealth.repository;

import com.example.smarthealth.model.auth.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByFirebaseUid(String firebaseUid);

    Page<User> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    Page<User> findByRole_Name(String roleName, Pageable pageable);

    @Query("select u.id from User u where u.isActive = true")
    List<Long> findAllActiveUserIds();

    Page<User> findByIsActive(boolean active, Pageable pageable);

    List<User> findTop10ByEmailIgnoreCaseStartingWith(String emailPrefix);

    // hoặc flexible hơn:
    List<User> findTop10ByEmailContainingIgnoreCase(String keyword);
}
