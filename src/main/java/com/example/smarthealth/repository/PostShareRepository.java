package com.example.smarthealth.repository;

import com.example.smarthealth.enums.PostVisibility;
import com.example.smarthealth.model.social.PostShare;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostShareRepository extends JpaRepository<PostShare, Long> {

    Page<PostShare> findByOriginalPostIdOrderByCreatedAtDesc(
            Long originalPostId,
            Pageable pageable);

    Page<PostShare> findByVisibilityAndSharedToGroupIdInOrderByCreatedAtDesc(
            PostVisibility visibility,
            List<Long> groupIds,
            Pageable pageable);

    Page<PostShare> findByVisibilityOrderByCreatedAtDesc(
            PostVisibility visibility,
            Pageable pageable);
}
