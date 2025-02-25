create table if not exists companies
(
    id         bigserial primary key,
    phone      varchar(13) unique,
    password   varchar(255),
    name       varchar(100) unique,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now()
);
