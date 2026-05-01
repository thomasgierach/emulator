create table if not exists users (
    user_pk bigserial primary key,
    username text not null unique,
    password_hash text not null,
    email text not null,
    user_role varchar(20) not null check (user_role in ('BASIC', 'ADMIN'))
);