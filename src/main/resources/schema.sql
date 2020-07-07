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
    invoice_id    BIGSERIAL PRIMARY KEY,
    user_id       BIGINT      NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    status        VARCHAR(20) NOT NULL,
    creation_date TIMESTAMP   NOT NULL,
    update_date   TIMESTAMP
);

CREATE TABLE order_items
(
    order_item_id   BIGSERIAL PRIMARY KEY,
    invoice_id      BIGINT REFERENCES invoices (invoice_id),
    periodical_id   BIGINT REFERENCES periodicals (id),
    price_per_month BIGINT NOT NULL
);


CREATE TABLE users_periodicals
(
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT REFERENCES users (user_id),
    periodical_id BIGINT REFERENCES periodicals (id)
);

INSERT INTO users(user_id, first_name, last_name, role, email, password_hash)
VALUES (DEFAULT, 'Jack', 'Nicholson', 'ADMIN', 'jack.nich@gmai.com', '1'),
       (DEFAULT, 'Marlon', 'Brando', 'USER', 'marl.brand@gmai.com', '2'),
       (DEFAULT, 'Robert', 'DeNiro', 'USER', 'rob.niro@gmai.com', '3'),
       (DEFAULT, 'Dustin', 'Hoffman', 'USER', 'dust.hoff@gmai.com', '4'),
       (DEFAULT, 'Al', 'Pacino', 'ADMIN', 'al.pach@gmai.com', '5'),
       (DEFAULT, 'Admin', 'Adm', 'ADMIN', 'admin.@mail.com',
        '$2y$10$5sF3fvgXPfxUNuYGcaGA3ekg//4qKiqIg1QBLGlDUpL8.gx7N5dKG'),
       (DEFAULT, 'John', 'Smith', 'USER', 'user.@mail.com',
        '$2y$10$5sF3fvgXPfxUNuYGcaGA3ekg//4qKiqIg1QBLGlDUpL8.gx7N5dKG');

INSERT INTO periodicals(name, description, monthly_price_cents)
VALUES ('Game Informer', '', 999),
       ('Better Homes and Gardens', '', 999),
       ('Reader''s Digest', '', 999),
       ('Good Housekeeping', '', 999),
       ('Family Circle', '', 999),
       ('National Geographic', '', 999),
       ('People Magazine', '', 999),
       ('Time Magazine', '', 999),
       ('Sports Illustrated', '', 999),
       ('Cosmopolitan', '', 999),
       ('Maxim', '', 999),
       ('Men''s Health', '', 999),
       ('Women''s Health', '', 999),
       ('Newsweek', '', 999),
       ('Rolling Stone', '', 999),
       ('Popular Science', '', 999),
       ('Vogue', '', 999),
       ('Playboy', '', 999),
       ('Popular Mechanics', '', 999),
       ('Forbes Magazine', '', 999),
       ('Fortune', '', 999),
       ('The Economist', '', 999),
       ('Wired', '', 999);

INSERT INTO invoices(user_id, status, creation_date, update_date)
VALUES (2, 'COMPLETED', '2020-04-10 20:36:56', '2020-04-11 9:30:56'),
       (2, 'COMPLETED', '2020-04-11 20:36:56', '2020-04-12 9:30:56'),
       (2, 'COMPLETED', '2020-04-12 20:36:56', '2020-04-13 9:30:56'),
       (2, 'COMPLETED', '2020-04-13 20:36:56', '2020-04-14 9:30:56'),
       (2, 'COMPLETED', '2020-04-14 20:36:56', '2020-04-15 9:30:56'),
       (2, 'COMPLETED', '2020-04-15 20:36:56', '2020-04-16 9:30:56'),
       (2, 'COMPLETED', '2020-04-16 20:36:56', '2020-04-17 9:30:56'),
       (2, 'IN_PROGRESS', '2020-04-23 16:44:09', NULL),
       (2, 'IN_PROGRESS', '2020-04-23 16:46:27', NULL),
       (2, 'IN_PROGRESS', '2020-04-23 17:00:32', NULL),
       (2, 'IN_PROGRESS', '2020-04-23 20:36:56', NULL);

INSERT INTO order_items(invoice_id, periodical_id, price_per_month)
VALUES (1, 3, 999),
       (1, 2, 999),
       (2, 4, 999),
       (2, 5, 999),
       (2, 6, 999),
       (3, 7, 999),
       (4, 8, 999),
       (5, 9, 999),
       (6, 10, 999),
       (7, 11, 999),
       (8, 12, 999),
       (9, 13, 999),
       (10, 14, 999),
       (10, 15, 999),
       (11, 16, 999),
       (11, 17, 999),
       (11, 18, 999);

INSERT INTO users_periodicals(user_id, periodical_id)
VALUES (2, 3),
       (2, 2),
       (2, 4),
       (2, 5);
