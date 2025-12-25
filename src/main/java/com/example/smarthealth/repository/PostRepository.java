package com.example.smarthealth.repository;

import com.example.smarthealth.enums.PostVisibility;
import com.example.smarthealth.model.social.Post;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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

        @Query("""
                        select p from Post p
                        where p.id = :postId
                          and (
                               p.visibility = 'PUBLIC'
                            or (
                                 p.visibility in ('GROUP', 'PRIVATE')
                                 and p.groupId is not null
                                 and exists (
                                     select 1 from GroupMember gm
                                     where gm.groupId = p.groupId
                                       and gm.userId = :userId
                                 )
                            )
                          )
                        """)
        Optional<Post> findReadableById(
                        @Param("postId") Long postId,
                        @Param("userId") Long userId);

        Page<Post> findByVisibility(PostVisibility visibility, Pageable pageable);

        @Query("""
                        select p from Post p
                        where p.groupId = :groupId
                          and p.visibility in ('GROUP', 'PRIVATE')
                          and exists (
                              select 1 from GroupMember gm
                              where gm.groupId = :groupId
                                and gm.userId = :userId
                          )
                        """)
        Page<Post> findGroupFeedReadable(
                        @Param("groupId") Long groupId,
                        @Param("userId") Long userId,
                        Pageable pageable);

        Page<Post> findByGroupIdAndVisibilityIn(Long groupId, List<PostVisibility> visibilities, Pageable pageable);

        @Query("""
                        select p from Post p
                        where p.id = :postId
                          and p.userId = :userId
                        """)
        Optional<Post> findOwnedById(@Param("postId") Long postId, @Param("userId") Long userId);

        @Query("""
                        select (count(gm) > 0) from GroupMember gm
                        where gm.groupId = :groupId and gm.userId = :userId
                        """)
        boolean isMember(@Param("groupId") Long groupId, @Param("userId") Long userId);

}
