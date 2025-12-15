ALTER TABLE posts
    DROP COLUMN IF EXISTS shared_post_id,
    DROP COLUMN IF EXISTS shared_to_group_id,
    DROP COLUMN IF EXISTS visibility;
