-- V1 - Initial schema for Smart Health Tracker

-- ---------- AUTH & USER MANAGEMENT ----------

CREATE TABLE roles (
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP
);

CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(255),
    avatar_url    VARCHAR(500),
    date_of_birth DATE,
    gender        VARCHAR(20),
    height_cm     INT,
    weight_kg     NUMERIC(5,2),
    role_id       INT NOT NULL REFERENCES roles(id),
    is_active     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP,
    updated_at    TIMESTAMP
);

CREATE TABLE device_tokens (
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT NOT NULL REFERENCES users(id),
    device_token VARCHAR(500) NOT NULL UNIQUE,
    platform     VARCHAR(20),
    last_used_at TIMESTAMP,
    created_at   TIMESTAMP
);

-- ---------- HEALTH TRACKING ----------

CREATE TABLE step_daily (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES users(id),
    date        DATE NOT NULL,
    total_steps INT NOT NULL DEFAULT 0,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    CONSTRAINT uq_step_daily_user_date UNIQUE (user_id, date)
);

CREATE TABLE sedentary_logs (
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT NOT NULL REFERENCES users(id),
    start_time       TIMESTAMP NOT NULL,
    end_time         TIMESTAMP NOT NULL,
    duration_minutes INT NOT NULL,
    steps_in_window  INT DEFAULT 0,
    created_at       TIMESTAMP
);

CREATE TABLE workout_sessions (
    id                       BIGSERIAL PRIMARY KEY,
    user_id                  BIGINT NOT NULL REFERENCES users(id),
    type                     VARCHAR(50) NOT NULL,
    start_time               TIMESTAMP NOT NULL,
    end_time                 TIMESTAMP NOT NULL,
    duration_seconds         INT,
    distance_meters          NUMERIC(10,2),
    avg_speed_mps            NUMERIC(10,3),
    avg_pace_sec_per_km      INT,
    calories                 INT,
    created_at               TIMESTAMP
);

CREATE TABLE workout_gps_points (
    id             BIGSERIAL PRIMARY KEY,
    workout_id     BIGINT NOT NULL REFERENCES workout_sessions(id),
    sequence_index INT NOT NULL,
    latitude       NUMERIC(9,6) NOT NULL,
    longitude      NUMERIC(9,6) NOT NULL,
    altitude       NUMERIC(8,2),
    timestamp      TIMESTAMP NOT NULL
);

CREATE INDEX idx_workout_points_workout_seq
    ON workout_gps_points (workout_id, sequence_index);

CREATE TABLE sleep_sessions (
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT NOT NULL REFERENCES users(id),
    start_time       TIMESTAMP NOT NULL,
    end_time         TIMESTAMP NOT NULL,
    duration_minutes INT,
    quality_level    VARCHAR(20),
    created_at       TIMESTAMP
);

CREATE TABLE heart_rate_records (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES users(id),
    measured_at TIMESTAMP NOT NULL,
    bpm         INT NOT NULL,
    note        VARCHAR(255),
    created_at  TIMESTAMP
);

-- ---------- NOTIFICATIONS & DAILY SUMMARY ----------

CREATE TABLE notifications (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT NOT NULL REFERENCES users(id),
    type       VARCHAR(50) NOT NULL,
    title      VARCHAR(255) NOT NULL,
    body       TEXT,
    sent_at    TIMESTAMP NOT NULL,
    read_at    TIMESTAMP,
    created_at TIMESTAMP
);

CREATE TABLE daily_health_summary (
    id                             BIGSERIAL PRIMARY KEY,
    user_id                        BIGINT NOT NULL REFERENCES users(id),
    date                           DATE NOT NULL,
    total_steps                    INT NOT NULL DEFAULT 0,
    total_sleep_minutes            INT NOT NULL DEFAULT 0,
    workout_count                  INT NOT NULL DEFAULT 0,
    total_workout_duration_minutes INT NOT NULL DEFAULT 0,
    avg_heart_rate                 INT,
    health_score                   INT,
    score_label                    VARCHAR(50),
    created_at                     TIMESTAMP,
    updated_at                     TIMESTAMP,
    CONSTRAINT uq_daily_summary_user_date UNIQUE (user_id, date)
);

-- ---------- SOCIAL: GROUPS, ACHIEVEMENTS, FEED ----------

CREATE TABLE groups (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    is_public   BOOLEAN NOT NULL DEFAULT TRUE,
    owner_id    BIGINT NOT NULL REFERENCES users(id),
    created_at  TIMESTAMP
);

CREATE TABLE group_members (
    id        BIGSERIAL PRIMARY KEY,
    group_id  BIGINT NOT NULL REFERENCES groups(id),
    user_id   BIGINT NOT NULL REFERENCES users(id),
    role      VARCHAR(20) NOT NULL,
    joined_at TIMESTAMP,
    CONSTRAINT uq_group_members UNIQUE (group_id, user_id)
);

CREATE TABLE achievement_definitions (
    id          BIGSERIAL PRIMARY KEY,
    code        VARCHAR(100) NOT NULL UNIQUE,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    rule_type   VARCHAR(50),
    rule_config TEXT,
    icon_url    VARCHAR(500),
    created_at  TIMESTAMP
);

CREATE TABLE user_achievements (
    id             BIGSERIAL PRIMARY KEY,
    user_id        BIGINT NOT NULL REFERENCES users(id),
    achievement_id BIGINT NOT NULL REFERENCES achievement_definitions(id),
    unlocked_at    TIMESTAMP NOT NULL,
    CONSTRAINT uq_user_achievements UNIQUE (user_id, achievement_id)
);

CREATE TABLE posts (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL REFERENCES users(id),
    content             TEXT NOT NULL,
    image_url           VARCHAR(500),
    achievement_user_id BIGINT REFERENCES user_achievements(id),
    created_at          TIMESTAMP,
    updated_at          TIMESTAMP
);

CREATE TABLE comments (
    id         BIGSERIAL PRIMARY KEY,
    post_id    BIGINT NOT NULL REFERENCES posts(id),
    user_id    BIGINT NOT NULL REFERENCES users(id),
    content    TEXT NOT NULL,
    created_at TIMESTAMP
);

CREATE TABLE post_likes (
    id         BIGSERIAL PRIMARY KEY,
    post_id    BIGINT NOT NULL REFERENCES posts(id),
    user_id    BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP,
    CONSTRAINT uq_post_likes UNIQUE (post_id, user_id)
);

CREATE TABLE challenges (
    id           BIGSERIAL PRIMARY KEY,
    code         VARCHAR(100) NOT NULL UNIQUE,
    name         VARCHAR(255) NOT NULL,
    description  TEXT,
    metric_type  VARCHAR(50) NOT NULL,
    target_value NUMERIC(10,2) NOT NULL,
    start_date   DATE NOT NULL,
    end_date     DATE NOT NULL,
    is_global    BOOLEAN NOT NULL DEFAULT TRUE,
    group_id     BIGINT REFERENCES groups(id),
    created_at   TIMESTAMP
);

CREATE TABLE challenge_participations (
    id                    BIGSERIAL PRIMARY KEY,
    challenge_id          BIGINT NOT NULL REFERENCES challenges(id),
    user_id               BIGINT NOT NULL REFERENCES users(id),
    joined_at             TIMESTAMP NOT NULL,
    current_value         NUMERIC(10,2) NOT NULL DEFAULT 0,
    completion_percentage NUMERIC(5,2) NOT NULL DEFAULT 0,
    is_completed          BOOLEAN NOT NULL DEFAULT FALSE,
    completed_at          TIMESTAMP,
    CONSTRAINT uq_challenge_participations UNIQUE (challenge_id, user_id)
);

-- ---------- SAFETY: FALL DETECTION & SOS ----------

CREATE TABLE fall_events (
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT NOT NULL REFERENCES users(id),
    detected_at  TIMESTAMP NOT NULL,
    confirmed    BOOLEAN NOT NULL DEFAULT FALSE,
    location_lat NUMERIC(9,6),
    location_lng NUMERIC(9,6),
    extra_data   TEXT,
    created_at   TIMESTAMP
);

CREATE TABLE emergency_contacts (
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT NOT NULL REFERENCES users(id),
    name         VARCHAR(255) NOT NULL,
    phone_number VARCHAR(50) NOT NULL,
    relationship VARCHAR(100),
    created_at   TIMESTAMP
);

CREATE TABLE sos_events (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL REFERENCES users(id),
    triggered_at        TIMESTAMP NOT NULL,
    location_lat        NUMERIC(9,6),
    location_lng        NUMERIC(9,6),
    handled_by_admin_id BIGINT REFERENCES users(id),
    handled_at          TIMESTAMP,
    status              VARCHAR(20) NOT NULL,
    created_at          TIMESTAMP
);
