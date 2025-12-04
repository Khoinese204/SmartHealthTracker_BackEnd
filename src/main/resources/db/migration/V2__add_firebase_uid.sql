ALTER TABLE users
ADD COLUMN IF NOT EXISTS firebase_uid VARCHAR(128),
ADD COLUMN IF NOT EXISTS auth_provider VARCHAR(50);

-- Đảm bảo mỗi firebase_uid là unique (nếu có)
CREATE UNIQUE INDEX IF NOT EXISTS ux_users_firebase_uid
ON users(firebase_uid)
WHERE firebase_uid IS NOT NULL;
