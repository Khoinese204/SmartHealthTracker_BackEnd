ALTER TABLE posts
ADD COLUMN group_id BIGINT NULL;

CREATE INDEX IF NOT EXISTS idx_posts_group_id_created_at
ON posts(group_id, created_at DESC);