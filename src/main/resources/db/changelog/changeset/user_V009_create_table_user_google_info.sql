CREATE TABLE if not exists user_google_info
(
    id             BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    sub            VARCHAR(255) NOT NULL UNIQUE,
    at_hash        VARCHAR(255),
    email_verified BOOLEAN,
    iss            VARCHAR(255),
    given_name     VARCHAR(255),
    nonce          VARCHAR(255),
    picture        VARCHAR(255),
    azp            VARCHAR(255),
    name           VARCHAR(255),
    exp            timestamptz DEFAULT current_timestamp,
    family_name    VARCHAR(255),
    iat            timestamptz DEFAULT current_timestamp,
    email          VARCHAR(255),
    user_id        bigint not null unique,

    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);