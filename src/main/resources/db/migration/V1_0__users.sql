create table if not exists users
(
    id         bigserial primary key,
    username   varchar(32)  not null unique,
    password   varchar(100) not null,
    created_at timestamp    not null default now(),
    updated_at timestamp    not null default now()
);