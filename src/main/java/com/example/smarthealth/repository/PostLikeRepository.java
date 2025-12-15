package com.example.smarthealth.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import com.example.smarthealth.model.social.PostLike;

import java.util.*;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByPostIdAndUserId(Long postId, Long userId);

    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);

    @Query("select pl.postId, count(pl) from PostLike pl where pl.postId in :postIds group by pl.postId")
    List<Object[]> countLikesByPostIds(List<Long> postIds);

    @Query("select pl.postId from PostLike pl where pl.userId = :userId and pl.postId in :postIds")
    List<Long> findLikedPostIds(Long userId, List<Long> postIds);

    Page<PostLike> findByPostIdOrderByCreatedAtDesc(Long postId, Pageable pageable);

    long countByPostId(Long postId);

}
