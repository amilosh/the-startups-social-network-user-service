ALTER TABLE users
    ADD COLUMN IF NOT EXISTS locale varchar(32) NOT NULL DEFAULT 'en-US';