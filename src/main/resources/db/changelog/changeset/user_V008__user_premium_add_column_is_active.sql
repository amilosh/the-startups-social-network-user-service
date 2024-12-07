ALTER TABLE user_premium
ADD COLUMN if not exists isActive boolean NOT NULL DEFAULT true;