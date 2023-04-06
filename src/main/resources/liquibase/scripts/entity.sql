-- liquibase formatted sql

--changeset sev:1
create table "users" (
    id         serial primary key,
    first_name text,
    last_name  text,
    email      text,
    phone      text,
    reg_date   date,
    admin_role boolean
);

--changeset sev:2
create table comments (
    id   serial primary key,
    text text,
    created_at timestamp
);
--changeset sev:3
create table ads
(
    id          serial primary key,
    title       text,
    description text,
    price       int
);

--changeset bm:1
alter table ads
    add column author_id int references "users"(id);

--changeset sev:4
alter table comments
ADD column ads_id int references ads(id);
alter table comments
add column user_id int references "users"(id);