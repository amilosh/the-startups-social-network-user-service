ALTER TABLE users
ADD COLUMN version INT NOT NULL DEFAULT 0;

ALTER TABLE users
ADD COLUMN banned BOOLEAN NOT NULL DEFAULT false;
