-- liquibase formatted sql

--changeset sev:1
create table "users" (
    id         serial primary key,
    first_name text,
    last_name  text,
    email      text,
    phone      text,
    reg_date   timestamp,
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

--changeset bm:2
alter table "users" rename column admin_role to role;
alter table "users" alter column role type text;

--changeset bm:3
alter table "users" add column password text;

--changeset bm:4
alter table "users" drop column reg_date;

--changeset sev:5
create table ads_images
(
id serial primary key,
path text,
file_size bigint,
media_type text
);
alter table ads
add column image_id int references ads_images(id);

--changeset bm:5
create table avatars (
                         id serial primary key,
                         file_path text,
                         file_size bigint,
                         media_type text
);

--changeset bm:6
alter table "users" add column avatar_id int references avatars(id);