DROP TABLE IF EXISTS todo_items;
DROP TABLE IF EXISTS todo_lists;
DROP TABLE IF EXISTS users;

CREATE TABLE users
(
  id       VARCHAR PRIMARY KEY,
  username VARCHAR NOT NULL
);

CREATE TABLE todo_lists
(
  id          VARCHAR PRIMARY KEY,
  title       VARCHAR,
  user_id     VARCHAR NOT NULL,
  is_in_trash BOOLEAN NOT NULL
);

CREATE TABLE todo_items
(
  id            VARCHAR PRIMARY KEY,
  description   VARCHAR,
  deadline_date  DATE,
  status        VARCHAR,
  is_done       BOOLEAN NOT NULL,
  todo_list_id  VARCHAR NOT NULL
);
