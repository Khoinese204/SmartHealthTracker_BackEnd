CREATE INDEX IF NOT EXISTS idx_post_likes_post_id_created_at ON post_likes(post_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_post_likes_user_id ON post_likes(user_id);