-- =========================
-- Seed MỞ KHÓA thành tích cho người dùng mẫu (user_id = 1)
-- =========================


-- =========================
-- Seed: step_daily => mở khóa 3 thành tích ACH_STEPS_10K_1D, ACH_STEPS_10K_7D, ACH_TOTAL_50K_7D
-- =========================

insert into step_daily (user_id, date, total_steps, created_at, updated_at)
select
  1,
  current_date - i,
  12000,
  now(),
  now()
from generate_series(0, 6) as i
on conflict (user_id, date)
do update
set total_steps = excluded.total_steps,
    updated_at = now();

-- =========================
-- Seed: workout_sessions
-- =========================
with t as (
  select
    date_trunc('week', now())::timestamp as mon,
    now()::timestamp as now_ts
),
base as (
  -- đảm bảo không seed trước Monday (đầu tuần)
  select greatest(mon + interval '2 hours', now_ts - interval '3 hours') as b
  from t
)
insert into workout_sessions
(user_id, type, start_time, end_time, duration_seconds, calories, created_at)
select 1, 'RUNNING',
       b,
       b + interval '20 minutes',
       1200, 150, now()
from base
union all
select 1, 'RUNNING',
       b + interval '1 hour',
       b + interval '1 hour 25 minutes',
       1500, 180, now()
from base
union all
select 1, 'RUNNING',
       b + interval '2 hours',
       b + interval '2 hours 30 minutes',
       1800, 200, now()
from base;
