CREATE TABLE users
(
    user_id       BIGSERIAL PRIMARY KEY,
    first_name    VARCHAR(100)        NOT NULL,
    last_name     VARCHAR(100)        NOT NULL,
    role          VARCHAR(20)         NOT NULL,
    email         VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(100)        NOT NULL
);

CREATE TABLE periodicals
(
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(100) NOT NULL,
    description         text,
    monthly_price_cents BIGINT       NOT NULL
);

CREATE TABLE invoices
(
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT      NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    status  VARCHAR(20) NOT NULL
--     creation_date TIMESTAMP   NOT NULL,
--     update_date   TIMESTAMP
);

CREATE TABLE order_items
(
    id              BIGSERIAL PRIMARY KEY,
    invoice_id      BIGINT REFERENCES invoices (id)    NOT NULL,
    periodicals_id  BIGINT REFERENCES periodicals (id) NOT NULL,
    price_per_month BIGINT                             NOT NULL
);


CREATE TABLE users_periodicals
(
    id              BIGSERIAL PRIMARY KEY,
    user_id       BIGINT REFERENCES users (user_id),
    periodical_id BIGINT REFERENCES periodicals (id)
);


INSERT INTO users(user_id, first_name, last_name, role, email, password_hash)
VALUES (DEFAULT, 'Jack', 'Nicholson', 'ADMIN', 'jack.nich@gmai.com', '1'),
       (DEFAULT, 'Marlon', 'Brando', 'USER', 'marl.brand@gmai.com', '2'),
       (DEFAULT, 'Robert', 'DeNiro', 'USER', 'rob.niro@gmai.com', '3'),
       (DEFAULT, 'Dustin', 'Hoffman', 'USER', 'dust.hoff@gmai.com', '4'),
       (DEFAULT, 'Al', 'Pacino', 'ADMIN', 'al.pach@gmai.com', '5');

INSERT INTO invoices(user_id, status)
VALUES (4, 'IN_PROGRESS'),
       (4, 'IN_PROGRESS'),
       (4, 'IN_PROGRESS'),
       (4, 'IN_PROGRESS');

INSERT INTO invoices(user_id, status)
VALUES (4, 'COMPLETED'),
       (3, 'COMPLETED'),
       (3, 'COMPLETED'),
       (2, 'COMPLETED');