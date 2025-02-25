create table if not exists companies
(
    id         bigserial primary key,
    phone      varchar(13)  not null unique,
    password   varchar(255) not null,
    name       varchar(100) not null unique,
    created_at timestamp    not null default now(),
    updated_at timestamp    not null default now()
);
