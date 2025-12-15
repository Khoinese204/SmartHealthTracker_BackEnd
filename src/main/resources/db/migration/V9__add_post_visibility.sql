ALTER TABLE posts
    ADD COLUMN IF NOT EXISTS visibility VARCHAR(20) NOT NULL DEFAULT 'PUBLIC';

CREATE INDEX IF NOT EXISTS idx_posts_visibility_created_at
    ON posts(visibility, created_at);