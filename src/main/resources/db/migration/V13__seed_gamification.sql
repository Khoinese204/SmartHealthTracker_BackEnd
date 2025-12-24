-- =========================
-- Seed: achievement_definitions
-- =========================
INSERT INTO achievement_definitions (code, name, description, rule_type, rule_config, icon_url, created_at)
VALUES
-- 1) 10k steps today
('ACH_STEPS_10K_1D', '10K Steps Today',
 'Reach at least 10,000 steps in a day.',
 'STEPS_DAILY_MINIMUM',
 '{"dailySteps":10000}',
 NULL, now()),

-- 2) 10k steps for 7 consecutive days
('ACH_STEPS_10K_7D', 'Consistency Master',
 'Reach at least 10,000 steps/day for 7 consecutive days.',
 'STEPS_DAILY_CONSECUTIVE_DAYS',
 '{"dailySteps":10000,"consecutiveDays":7}',
 NULL, now()),

-- 3) 50k total steps in last 7 days
('ACH_TOTAL_50K_7D', 'Weekly Grinder',
 'Reach at least 50,000 total steps in the last 7 days.',
 'TOTAL_STEPS_LAST_DAYS',
 '{"target":50000,"lastDays":7}',
 NULL, now()),

-- 4) 3 workouts in this week (Mon..Now)
('ACH_WORKOUT_3W', 'Workout Streak',
 'Complete at least 3 workout sessions in a week.',
 'WORKOUTS_IN_WEEK',
 '{"count":3}',
 NULL, now())
ON CONFLICT (code) DO NOTHING;

-- =========================
-- Seed: challenges
-- =========================
INSERT INTO challenges (code, name, description, metric_type, target_value, start_date, end_date, is_global, group_id, created_at)
VALUES
-- 1) Walk 50,000 steps in 7 days (today..today+6)
('CH_STEPS_50K_7D', 'Walk 50,000 steps in 7 days',
 'Accumulate 50,000 steps within 7 days.',
 'STEPS', 50000,
 current_date, current_date + 6,
 TRUE, NULL, now()),

-- 2) Complete 3 workouts in current week (Mon..Sun)
('CH_WORKOUT_3W', 'Complete 3 workouts this week',
 'Finish 3 workout sessions from Monday to Sunday.',
 'WORKOUT_COUNT', 3,
 date_trunc('week', current_date)::date,
 (date_trunc('week', current_date)::date + 6),
 TRUE, NULL, now())
ON CONFLICT (code) DO NOTHING;