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