ALTER TABLE user_premium
    ADD COLUMN IF NOT EXISTS premium_type varchar(10) NOT NULL;