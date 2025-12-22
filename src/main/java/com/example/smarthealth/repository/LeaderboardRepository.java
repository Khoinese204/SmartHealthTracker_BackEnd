package com.example.smarthealth.repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class LeaderboardRepository {

    @PersistenceContext
    private final EntityManager em;

    public List<Object[]> globalSteps(LocalDate from, LocalDate to, int limit) {
        Query q = em.createNativeQuery("""
                    select u.id as user_id,
                           coalesce(u.full_name, u.email) as name,
                           coalesce(sum(sd.total_steps),0) as value
                    from users u
                    left join step_daily sd
                      on sd.user_id = u.id and sd.date between :from and :to
                    where u.is_active = true
                    group by u.id, u.full_name, u.email
                    order by value desc
                    limit :limit
                """);

        q.setParameter("from", from);
        q.setParameter("to", to);
        q.setParameter("limit", limit);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = q.getResultList();
        return normalize(rows);
    }

    public List<Object[]> groupSteps(Long groupId, LocalDate from, LocalDate to, int limit) {
        Query q = em.createNativeQuery("""
                    select u.id as user_id,
                           coalesce(u.full_name, u.email) as name,
                           coalesce(sum(sd.total_steps),0) as value
                    from group_members gm
                    join users u on u.id = gm.user_id
                    left join step_daily sd
                      on sd.user_id = u.id and sd.date between :from and :to
                    where gm.group_id = :groupId and u.is_active = true
                    group by u.id, u.full_name, u.email
                    order by value desc
                    limit :limit
                """);

        q.setParameter("groupId", groupId);
        q.setParameter("from", from);
        q.setParameter("to", to);
        q.setParameter("limit", limit);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = q.getResultList();
        return normalize(rows);
    }

    /**
     * PostgreSQL native query thường trả id kiểu BigInteger/Long tuỳ driver.
     * Normalize để service xử lý ổn định.
     */
    private List<Object[]> normalize(List<Object[]> rows) {
        List<Object[]> out = new ArrayList<>(rows.size());
        for (Object[] r : rows) {
            Object userIdRaw = r[0];
            Long userId;
            if (userIdRaw instanceof BigInteger bi)
                userId = bi.longValue();
            else if (userIdRaw instanceof Number n)
                userId = n.longValue();
            else
                userId = Long.valueOf(userIdRaw.toString());

            String name = r[1] == null ? null : r[1].toString();

            long value;
            Object valueRaw = r[2];
            if (valueRaw instanceof BigInteger bi)
                value = bi.longValue();
            else if (valueRaw instanceof Number n)
                value = n.longValue();
            else
                value = Long.parseLong(valueRaw.toString());

            out.add(new Object[] { userId, name, value });
        }
        return out;
    }
}
