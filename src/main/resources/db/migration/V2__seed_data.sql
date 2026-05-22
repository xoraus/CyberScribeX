-- ============================================
-- CyberScribeX Seed Data Migration
-- ============================================

INSERT INTO roles (name) VALUES
    ('ROLE_ADMIN'),
    ('ROLE_USER')
ON DUPLICATE KEY UPDATE name = name;
