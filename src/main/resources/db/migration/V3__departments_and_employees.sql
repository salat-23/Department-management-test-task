CREATE TABLE departments
(
    id      BIGSERIAL NOT NULL PRIMARY KEY,
    name    VARCHAR   NOT NULL,
    code    VARCHAR   NOT NULL UNIQUE,
    head_id BIGSERIAL NOT NULL REFERENCES users (id)
);

CREATE TABLE employees
(
    id        BIGSERIAL NOT NULL PRIMARY KEY,
    full_name VARCHAR   NOT NULL,
    user_id   BIGSERIAL NOT NULL REFERENCES users (id)
);

CREATE TABLE employee_department_junctions
(
    id            BIGSERIAL NOT NULL PRIMARY KEY,
    department_id BIGSERIAL NOT NULL REFERENCES departments (id),
    employee_id   BIGSERIAL NOT NULL REFERENCES employees (id),
    wage_rate     BIGINT    NOT NULL,
    currency      VARCHAR   NOT NULL
);

CREATE TABLE pay_props
(
    id                              BIGSERIAL NOT NULL PRIMARY KEY,
    value                           VARCHAR   NOT NULL,
    type                            VARCHAR   NOT NULL,
    employee_department_junction_id BIGSERIAL NOT NULL REFERENCES employee_department_junctions (id),
    UNIQUE (value, type, employee_department_junction_id)
);