-- V4: Add missing fields to users table (phone, tokens, login attempts, lock, etc.)
ALTER TABLE users
    ADD COLUMN phone                    VARCHAR(255)            DEFAULT NULL AFTER full_name,
    ADD COLUMN verification_token       VARCHAR(255)            DEFAULT NULL AFTER deleted_at,
    ADD COLUMN verification_token_expiry DATETIME               DEFAULT NULL AFTER verification_token,
    ADD COLUMN reset_token              VARCHAR(255)            DEFAULT NULL AFTER verification_token_expiry,
    ADD COLUMN reset_token_expiry       DATETIME                DEFAULT NULL AFTER reset_token,
    ADD COLUMN token_version            INT         NOT NULL    DEFAULT 0 AFTER reset_token_expiry,
    ADD COLUMN login_attempts           INT         NOT NULL    DEFAULT 0 AFTER token_version,
    ADD COLUMN lock_until               DATETIME                DEFAULT NULL AFTER login_attempts;
