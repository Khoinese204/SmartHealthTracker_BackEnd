package com.example.smarthealth.repository;

import com.example.smarthealth.enums.PostVisibility;
import com.example.smarthealth.model.social.Post;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Collection;

public interface PostRepository extends JpaRepository<Post, Long> {

    // case: có groupIds
    @Query("""
                select p
                from Post p
                where
                    p.visibility = :publicVis
                    or (p.visibility = :privateVis and p.userId = :meId)
                    or (p.visibility = :groupVis and p.groupId in :groupIds)
            """)
    Page<Post> findVisibleFeed(
            @Param("meId") Long meId,
            @Param("groupIds") Collection<Long> groupIds,
            @Param("publicVis") PostVisibility publicVis,
            @Param("privateVis") PostVisibility privateVis,
            @Param("groupVis") PostVisibility groupVis,
            Pageable pageable);

    // case: không có groupIds (tránh IN () lỗi)
    @Query("""
                select p
                from Post p
                where
                    p.visibility = :publicVis
                    or (p.visibility = :privateVis and p.userId = :meId)
            """)
    Page<Post> findVisibleFeedNoGroups(
            @Param("meId") Long meId,
            @Param("publicVis") PostVisibility publicVis,
            @Param("privateVis") PostVisibility privateVis,
            Pageable pageable);
}