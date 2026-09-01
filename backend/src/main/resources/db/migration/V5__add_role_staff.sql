-- V5: Add ROLE_STAFF to users.role ENUM values and add missing login_attempts/register_attempts tables
ALTER TABLE users MODIFY COLUMN role ENUM('ROLE_USER','ROLE_STAFF','ROLE_ADMIN','ROLE_INTERVIEWER') NOT NULL DEFAULT 'ROLE_USER';

CREATE TABLE IF NOT EXISTS login_attempts (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    ip_address VARCHAR(255),
    success TINYINT(1) NOT NULL,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_login_ip (ip_address),
    INDEX idx_login_email_created (email, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS register_attempts (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT,
    account VARCHAR(255),
    address VARCHAR(255),
    counter_fail INT NOT NULL DEFAULT 0,
    timestamp DATETIME,
    locked TINYINT(1) DEFAULT 0,
    lock_until DATETIME,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
