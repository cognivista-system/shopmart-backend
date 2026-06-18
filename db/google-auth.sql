-- ============================================================================
-- ShopMart — "Sign in with Google" columns on the users table
-- Apply after schema.sql. (ddl-auto=update also adds these automatically.)
-- ============================================================================
ALTER TABLE users ADD COLUMN IF NOT EXISTS provider    VARCHAR(255) NOT NULL DEFAULT 'LOCAL';  -- LOCAL | GOOGLE
ALTER TABLE users ADD COLUMN IF NOT EXISTS provider_id VARCHAR(255);
CREATE INDEX IF NOT EXISTS idx_users_provider_id ON users (provider_id);
