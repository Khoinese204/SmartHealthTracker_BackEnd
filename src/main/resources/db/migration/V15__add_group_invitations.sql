CREATE TABLE group_invites (
    id                 BIGSERIAL PRIMARY KEY,
    group_id           BIGINT NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    invited_user_id    BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    invited_by_user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,

    relation           VARCHAR(30) NOT NULL,  -- FATHER/MOTHER/BROTHER/SISTER/OTHER
    status             VARCHAR(20) NOT NULL,  -- PENDING/ACCEPTED/DECLINED/EXPIRED
    created_at         TIMESTAMP NOT NULL DEFAULT NOW(),
    expires_at         TIMESTAMP,

    CONSTRAINT uq_group_invite_pending UNIQUE (group_id, invited_user_id, status)
);

CREATE INDEX idx_group_invites_invited_user_status
    ON group_invites (invited_user_id, status);

CREATE INDEX idx_group_invites_group
    ON group_invites (group_id);