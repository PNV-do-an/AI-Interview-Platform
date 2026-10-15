-- V3: Create admin_audit_log table
CREATE TABLE IF NOT EXISTS admin_audit_log (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    admin_id        BIGINT          NOT NULL,
    admin_email     VARCHAR(255)    NOT NULL,
    target_user_id  BIGINT,
    action          VARCHAR(100)    NOT NULL,
    detail          TEXT,
    created_at      DATETIME        NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_audit_admin_id (admin_id),
    INDEX idx_audit_target_user_id (target_user_id),
    INDEX idx_audit_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
