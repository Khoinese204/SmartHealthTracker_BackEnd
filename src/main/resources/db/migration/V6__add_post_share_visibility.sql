ALTER TABLE posts
    ADD COLUMN shared_post_id BIGINT REFERENCES posts(id),
    ADD COLUMN shared_to_group_id BIGINT REFERENCES groups(id),
    ADD COLUMN visibility VARCHAR(20) NOT NULL DEFAULT 'PUBLIC';

CREATE INDEX idx_posts_visibility_created_at ON posts(visibility, created_at);
CREATE INDEX idx_posts_shared_post_id ON posts(shared_post_id);
CREATE INDEX idx_posts_shared_to_group_id ON posts(shared_to_group_id);