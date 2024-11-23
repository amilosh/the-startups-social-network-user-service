ALTER TABLE users
    ADD COLUMN IF NOT EXISTS rank_score DECIMAL NOT NULL
    DEFAULT 0.0
    CHECK (rank_score >= 0 AND rank_score <= 100);