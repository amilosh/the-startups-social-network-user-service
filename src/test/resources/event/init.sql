CREATE SCHEMA postgres;

DROP TABLE IF EXISTS project_subscription;
DROP TABLE IF EXISTS contact;
DROP TABLE IF EXISTS skill_request;
DROP TABLE IF EXISTS recommendation_request;
DROP TABLE IF EXISTS skill_offer;
DROP TABLE IF EXISTS recommendation;
DROP TABLE IF EXISTS skill_guarantee;
DROP TABLE IF EXISTS user_skill;
DROP TABLE IF EXISTS mentorship_request;
DROP TABLE IF EXISTS mentorship;
DROP TABLE IF EXISTS subscription;
DROP TABLE IF EXISTS goal_invitation;
DROP TABLE IF EXISTS user_goal;
DROP TABLE IF EXISTS goal_skill;
DROP TABLE IF EXISTS event_skill;
DROP TABLE IF EXISTS user_event;
DROP TABLE IF EXISTS rating;
DROP TABLE IF EXISTS event;
DROP TABLE IF EXISTS goal;
DROP TABLE IF EXISTS skill;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS country;

CREATE TABLE country (
                         id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                         title varchar(64) UNIQUE NOT NULL
);

CREATE TABLE users (
                       id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                       username varchar(64) UNIQUE NOT NULL,
                       password varchar(128) NOT NULL,
                       email varchar(64) UNIQUE NOT NULL,
                       phone varchar(32) UNIQUE,
                       about_me varchar(4096),
                       active boolean DEFAULT true NOT NULL,
                       city varchar(64),
                       country_id bigint NOT NULL,
                       experience int,
                       created_at timestamptz DEFAULT current_timestamp,
                       updated_at timestamptz DEFAULT current_timestamp,

                       CONSTRAINT fk_country_id FOREIGN KEY (country_id) REFERENCES country (id)
);

CREATE TABLE subscription (
                              id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                              follower_id bigint NOT NULL,
                              followee_id bigint NOT NULL,
                              created_at timestamptz DEFAULT current_timestamp,
                              updated_at timestamptz DEFAULT current_timestamp,

                              CONSTRAINT fk_follower_id FOREIGN KEY (follower_id) REFERENCES users (id),
                              CONSTRAINT fk_followee_id FOREIGN KEY (followee_id) REFERENCES users (id)
);

CREATE TABLE mentorship (
                            id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                            mentor_id bigint NOT NULL,
                            mentee_id bigint NOT NULL,
                            created_at timestamptz DEFAULT current_timestamp,
                            updated_at timestamptz DEFAULT current_timestamp,

                            CONSTRAINT fk_mentor_id FOREIGN KEY (mentor_id) REFERENCES users (id),
                            CONSTRAINT fk_mentee_id FOREIGN KEY (mentee_id) REFERENCES users (id)
);

CREATE TABLE mentorship_request (
                                    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                                    description varchar(4096) NOT NULL,
                                    requester_id bigint NOT NULL,
                                    receiver_id bigint NOT NULL,
                                    status smallint DEFAULT 0 NOT NULL,
                                    rejection_reason varchar(4096),
                                    created_at timestamptz DEFAULT current_timestamp,
                                    updated_at timestamptz DEFAULT current_timestamp,

                                    CONSTRAINT fk_mentee_req_id FOREIGN KEY (requester_id) REFERENCES users (id),
                                    CONSTRAINT fk_mentor_req_id FOREIGN KEY (receiver_id) REFERENCES users (id)
);

CREATE TABLE skill (
                       id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                       title varchar(64) UNIQUE NOT NULL,
                       created_at timestamptz DEFAULT current_timestamp,
                       updated_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE user_skill (
                            id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                            user_id bigint NOT NULL,
                            skill_id bigint NOT NULL,
                            created_at timestamptz DEFAULT current_timestamp,
                            updated_at timestamptz DEFAULT current_timestamp,

                            CONSTRAINT fk_user_skill_id FOREIGN KEY (user_id) REFERENCES users (id),
                            CONSTRAINT fk_skill_user_id FOREIGN KEY (skill_id) REFERENCES skill (id)
);

CREATE TABLE user_skill_guarantee (
                                      id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                                      user_id BIGINT NOT NULL,
                                      skill_id BIGINT NOT NULL,
                                      guarantor_id BIGINT NOT NULL,

                                      CONSTRAINT fk_user_skill_guarantee_user FOREIGN KEY (user_id) REFERENCES users (id),
                                      CONSTRAINT fk_user_skill_guarantee_skill FOREIGN KEY (skill_id) REFERENCES skill (id),
                                      CONSTRAINT fk_user_skill_guarantee_guarantor FOREIGN KEY (guarantor_id) REFERENCES users (id)
);

CREATE TABLE recommendation (
                                id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                                content varchar(4096) NOT NULL,
                                author_id bigint NOT NULL,
                                receiver_id bigint NOT NULL,
                                created_at timestamptz DEFAULT current_timestamp,
                                updated_at timestamptz DEFAULT current_timestamp,

                                CONSTRAINT fk_recommender_id FOREIGN KEY (author_id) REFERENCES users (id),
                                CONSTRAINT fk_receiver_id FOREIGN KEY (receiver_id) REFERENCES users (id)
);

CREATE TABLE skill_offer (
                             id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                             skill_id bigint NOT NULL,
                             recommendation_id bigint NOT NULL,

                             CONSTRAINT fk_skill_offered_id FOREIGN KEY (skill_id) REFERENCES skill (id) ON DELETE CASCADE,
                             CONSTRAINT fk_recommendation_skill_id FOREIGN KEY (recommendation_id) REFERENCES recommendation (id) ON DELETE CASCADE
);

CREATE TABLE recommendation_request (
                                        id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                                        message varchar(4096) NOT NULL,
                                        requester_id bigint NOT NULL,
                                        receiver_id bigint NOT NULL,
                                        status smallint DEFAULT 0 NOT NULL,
                                        rejection_reason varchar(4096),
                                        recommendation_id bigint,
                                        created_at timestamptz DEFAULT current_timestamp,
                                        updated_at timestamptz DEFAULT current_timestamp,

                                        CONSTRAINT fk_requester_recommendation_id FOREIGN KEY (requester_id) REFERENCES users (id),
                                        CONSTRAINT fk_receiver_recommendation_id FOREIGN KEY (receiver_id) REFERENCES users (id),
                                        CONSTRAINT fk_recommendation_req_id FOREIGN KEY (recommendation_id) REFERENCES recommendation (id) ON DELETE CASCADE
);

CREATE TABLE skill_request (
                               id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                               request_id bigint NOT NULL,
                               skill_id bigint NOT NULL,

                               CONSTRAINT fk_request_skill_id FOREIGN KEY (request_id) REFERENCES recommendation_request (id) ON DELETE CASCADE,
                               CONSTRAINT fk_skill_request_id FOREIGN KEY (skill_id) REFERENCES skill (id)
);

CREATE TABLE contact (
                         id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                         user_id bigint NOT NULL,
                         contact varchar(128) NOT NULL UNIQUE,
                         type smallint NOT NULL,

                         CONSTRAINT fk_contact_owner_id FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE project_subscription (
                                      id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                                      project_id bigint NOT NULL,
                                      follower_id bigint NOT NULL,
                                      created_at timestamptz DEFAULT current_timestamp,
                                      updated_at timestamptz DEFAULT current_timestamp,

                                      CONSTRAINT fk_project_follower_id FOREIGN KEY (follower_id) REFERENCES users (id)
);

CREATE TABLE event (
                       id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                       title varchar(64) NOT NULL,
                       description varchar(4096) NOT NULL,
                       start_date timestamptz NOT NULL,
                       end_date timestamptz NOT NULL,
                       location varchar(128) NOT NULL,
                       max_attendees int,
                       user_id bigint NOT NULL,
                       type smallint NOT NULL,
                       status smallint NOT NULL DEFAULT 0,
                       created_at timestamptz DEFAULT current_timestamp,
                       updated_at timestamptz DEFAULT current_timestamp,

                       CONSTRAINT fk_event_owner_id FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE event_skill (
                             id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                             event_id bigint NOT NULL,
                             skill_id bigint NOT NULL,

                             CONSTRAINT fk_event_skill_id FOREIGN KEY (event_id) REFERENCES event (id) ON DELETE CASCADE,
                             CONSTRAINT fk_skill_event_id FOREIGN KEY (skill_id) REFERENCES skill (id)
);

CREATE TABLE user_event (
                            id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                            user_id bigint NOT NULL,
                            event_id bigint NOT NULL,

                            CONSTRAINT fk_user_event_id FOREIGN KEY (user_id) REFERENCES users (id),
                            CONSTRAINT fk_event_user_id FOREIGN KEY (event_id) REFERENCES event (id)
);

CREATE TABLE rating (
                        id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                        user_id bigint NOT NULL,
                        event_id bigint NOT NULL,
                        rate smallint NOT NULL,
                        comment varchar(4096),
                        created_at timestamptz DEFAULT current_timestamp,
                        updated_at timestamptz DEFAULT current_timestamp,

                        CONSTRAINT fk_rater_id FOREIGN KEY (user_id) REFERENCES users (id),
                        CONSTRAINT fk_event_rated_id FOREIGN KEY (event_id) REFERENCES event (id)
);

ALTER TABLE users
    ADD COLUMN if not exists profile_pic_file_id text,
    ADD COLUMN if not exists profile_pic_small_file_id text;

CREATE TABLE if not exists content_data (
                                            id bigint PRIMARY key GENERATED ALWAYS AS IDENTITY UNIQUE,
                                            content oid
);

CREATE TABLE contact_preferences (
                                     id bigint PRIMARY key GENERATED ALWAYS AS IDENTITY UNIQUE,
                                     user_id bigint NOT NULL,
                                     preference smallint NOT NULL,

                                     CONSTRAINT fk_contact_preferences_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE goal (
                      id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                      title varchar(64) NOT NULL,
                      description varchar(4096) NOT NULL,
                      parent_goal_id bigint,
                      status smallint DEFAULT 0 NOT NULL,
                      deadline timestamptz,
                      created_at timestamptz DEFAULT current_timestamp,
                      updated_at timestamptz DEFAULT current_timestamp,
                      mentor_id bigint,

                      CONSTRAINT fk_goal_id FOREIGN KEY (parent_goal_id) REFERENCES goal (id),
                      CONSTRAINT fk_mentor_id FOREIGN KEY (mentor_id) REFERENCES users (id)
);

CREATE TABLE goal_invitation (
                                 id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                                 goal_id bigint NOT NULL,
                                 inviter_id bigint NOT NULL,
                                 invited_id bigint NOT NULL,
                                 status smallint DEFAULT 0 NOT NULL,
                                 created_at timestamptz DEFAULT current_timestamp,
                                 updated_at timestamptz DEFAULT current_timestamp,

                                 CONSTRAINT fk_inviter_id FOREIGN KEY (inviter_id) REFERENCES users (id),
                                 CONSTRAINT fk_invited_id FOREIGN KEY (invited_id) REFERENCES users (id),
                                 CONSTRAINT fk_goal_id FOREIGN KEY (goal_id) REFERENCES goal (id)
);

CREATE TABLE user_goal (
                           id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                           user_id bigint NOT NULL,
                           goal_id bigint NOT NULL,
                           created_at timestamptz DEFAULT current_timestamp,
                           updated_at timestamptz DEFAULT current_timestamp,

                           CONSTRAINT fk_user_goal_id FOREIGN KEY (user_id) REFERENCES users (id),
                           CONSTRAINT fk_goal_user_id FOREIGN KEY (goal_id) REFERENCES goal (id)
);

CREATE TABLE goal_skill (
                            id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                            goal_id bigint NOT NULL,
                            skill_id bigint NOT NULL,
                            created_at timestamptz DEFAULT current_timestamp,
                            updated_at timestamptz DEFAULT current_timestamp,

                            CONSTRAINT fk_goal_skill_id FOREIGN KEY (goal_id) REFERENCES goal (id),
                            CONSTRAINT fk_skill_goal_id FOREIGN KEY (skill_id) REFERENCES skill (id)
);

CREATE TABLE user_premium (
                              id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                              user_id bigint NOT NULL,
                              start_date timestamptz NOT NULL DEFAULT current_timestamp,
                              end_date timestamptz NOT NULL,

                              CONSTRAINT fk_user_premium_id FOREIGN KEY (user_id) REFERENCES users (id)
);

INSERT INTO country (title)
VALUES
    ('United States'),
    ('United Kingdom'),
    ('Australia'),
    ('France');

INSERT INTO users (username, email, phone, password, active, about_me, country_id, city, experience, created_at, updated_at)
VALUES
    ('JohnDoe', 'johndoe@example.com', '1234567890', 'password1', true, 'About John Doe', 1, 'New York', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('JaneSmith', 'janesmith@example.com', '0987654321', 'password2', true, 'About Jane Smith', 2, 'London', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('MichaelJohnson', 'michaeljohnson@example.com', '1112223333', 'password3', true, 'About Michael Johnson', 1, 'Sydney', 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('EmilyDavis', 'emilydavis@example.com', '4445556666', 'password4', true, 'About Emily Davis', 3, 'Paris', 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('WilliamTaylor', 'williamtaylor@example.com', '7778889999', 'password5', true, 'About William Taylor', 2, 'Toronto', 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('OliviaAnderson', 'oliviaanderson@example.com', '0001112222', 'password6', true, 'About Olivia Anderson', 1, 'Berlin', 12, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('JamesWilson', 'jameswilson@example.com', '3334445555', 'password7', true, 'About James Wilson', 3, 'Tokyo', 14, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('SophiaMartin', 'sophiamartin@example.com', '6667778888', 'password8', true, 'About Sophia Martin', 4, 'Rome', 16, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('BenjaminThompson', 'benjaminthompson@example.com', '9990001111', 'password9', true, 'About Benjamin Thompson', 4, 'Moscow', 18, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('AvaHarris', 'avaharris@example.com', '2223334444', 'password10', true, 'About Ava Harris', 3, 'Madrid', 20, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO event (title, description, start_date, end_date, location, max_attendees, user_id, type, status, created_at, updated_at)
VALUES
    ('Тест1', 'Тестовый ивент1', '2024-10-30 14:20:50.317000 +00:00', '2024-08-30 14:22:06.571000 +00:00', 'Moscow', 30, 1, 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Тест2', 'Тестовый ивент2', '2024-10-30 14:20:50.317000 +00:00', '2024-08-30 14:22:06.571000 +00:00', 'New York', 30, 1, 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Тест3', 'Тестовый ивент3', '2024-10-30 14:20:50.317000 +00:00', '2024-08-30 14:22:06.571000 +00:00', 'Berlin', 30, 1, 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Тест4', 'Тестовый ивент4', '2024-10-30 14:20:50.317000 +00:00', '2024-08-30 14:22:06.571000 +00:00', 'Paris', 30, 1, 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Тест5', 'Тестовый ивент5', '2024-10-30 14:20:50.317000 +00:00', '2024-08-30 14:22:06.571000 +00:00', 'Dubai', 30, 1, 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO user_event (user_id, event_id)
VALUES
    (1,3),
    (5,2),
    (6,2),
    (7,2),
    (7,5);