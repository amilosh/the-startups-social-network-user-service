ALTER TABLE user_premium
    ADD COLUMN if not exists premium_period smallint DEFAULT 0 NOT NULL