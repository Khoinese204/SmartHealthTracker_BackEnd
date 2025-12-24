package com.example.smarthealth.repository;

import com.example.smarthealth.model.social.PostComment;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    Page<PostComment> findByPostIdOrderByCreatedAtDesc(Long postId, Pageable pageable);

    @Query("select c.postId, count(c) from PostComment c where c.postId in :postIds group by c.postId")
    List<Object[]> countCommentsByPostIds(List<Long> postIds);

    long countByPostId(Long postId);
}
