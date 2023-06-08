CREATE TABLE users
(
    id       BIGSERIAL NOT NULL PRIMARY KEY,
    login    VARCHAR   NOT NULL UNIQUE,
    email    VARCHAR   NOT NULL UNIQUE,
    password VARCHAR   NOT NULL,
    active   BOOLEAN   NOT NULL
);

CREATE TABLE users_roles
(
    user_id BIGSERIAL NOT NULL REFERENCES users (id),
    role    VARCHAR,
    PRIMARY KEY (user_id, role)
);

INSERT INTO users (id, login, email, password, active)
VALUES (1, 'admin', 'admin@admin.com', '$2a$10$yJ4DEajhbZsvi9MC81PkiuCkRDHJG6JiiLv4ix.3mmKkiTlRxOCUi', TRUE);

INSERT INTO users_roles(user_id, role)
VALUES (1, 'ADMIN');
