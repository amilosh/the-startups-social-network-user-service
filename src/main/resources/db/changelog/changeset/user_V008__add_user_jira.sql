CREATE TABLE user_jira (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    user_id bigint NOT NULL,
    jira_domain varchar(64) NOT NULL,
    jira_email varchar(64) NOT NULL,
    jira_account_id varchar(128) NOT NULL,
    jira_token varchar(256) NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,

    CONSTRAINT unique_user_jira UNIQUE (user_id, jira_domain),
    CONSTRAINT fk_jira_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);