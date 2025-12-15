CREATE TABLE post_shares (
    id BIGSERIAL PRIMARY KEY,

    original_post_id BIGINT NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    shared_by_user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,

    visibility VARCHAR(20) NOT NULL, -- PUBLIC | PRIVATE | GROUP
    shared_to_group_id BIGINT NULL REFERENCES groups(id) ON DELETE SET NULL,

    message TEXT NULL, -- optional caption khi share
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_post_shares_original_post_id_created_at
    ON post_shares(original_post_id, created_at DESC);

CREATE INDEX idx_post_shares_shared_by_user_id_created_at
    ON post_shares(shared_by_user_id, created_at DESC);

CREATE INDEX idx_post_shares_visibility_created_at
    ON post_shares(visibility, created_at DESC);

CREATE INDEX idx_post_shares_shared_to_group_id_created_at
    ON post_shares(shared_to_group_id, created_at DESC);
