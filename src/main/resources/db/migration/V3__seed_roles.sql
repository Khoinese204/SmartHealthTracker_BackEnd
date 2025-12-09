-- V3__seed_roles.sql
INSERT INTO roles (name, description)
VALUES ('USER', 'Normal application user')
ON CONFLICT (name) DO NOTHING;
