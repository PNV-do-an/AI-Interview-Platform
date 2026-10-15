-- V2: Add locked and deleted_at fields to users table
ALTER TABLE users
    ADD COLUMN locked     TINYINT(1) NOT NULL DEFAULT 0 AFTER enabled,
    ADD COLUMN deleted_at DATETIME           DEFAULT NULL AFTER updated_at;
