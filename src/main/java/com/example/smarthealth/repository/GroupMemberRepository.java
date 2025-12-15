package com.example.smarthealth.repository;

import com.example.smarthealth.model.social.GroupMember;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    boolean existsByGroupIdAndUserId(Long groupId, Long userId);

    Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId);

    List<GroupMember> findByGroupIdOrderByJoinedAtAsc(Long groupId);

    @Query("""
                select gm.groupId
                from GroupMember gm
                where gm.userId = :userId
            """)
    List<Long> findGroupIdsByUserId(@Param("userId") Long userId);

    @Query("select gm.groupId, count(gm) from GroupMember gm where gm.groupId in :groupIds group by gm.groupId")
    List<Object[]> countMembersByGroupIds(List<Long> groupIds);
}
