CREATE TABLE users
(
    id       BIGSERIAL NOT NULL PRIMARY KEY,
    email    VARCHAR   NOT NULL UNIQUE,
    password VARCHAR   NOT NULL,
    active   BOOLEAN   NOT NULL
);

CREATE TABLE users_roles
(
    user_id BIGSERIAL NOT NULL REFERENCES users (id),
    role    VARCHAR
);

INSERT INTO users (id, email, password, active)
VALUES (1, 'admin@admin.com', '$2a$10$yJ4DEajhbZsvi9MC81PkiuCkRDHJG6JiiLv4ix.3mmKkiTlRxOCUi', true);

INSERT INTO users_roles(user_id, role) VALUES (1, 'ADMIN');
