-- ============================================
-- KKP SKATE CROSS
-- MIGRATION V11: DEFAULT ROLE
-- ============================================

INSERT INTO roles (
    code,
    name,
    description
)
VALUES (
    'SUPER_ADMIN',
    'Super Admin',
    'Administrator sistem Skate Cross'
)
ON CONFLICT (code) DO NOTHING;