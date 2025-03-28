create table if not exists media_files
(

    id         bigserial primary key,
    url        varchar(255) not null,
    created_at timestamp    not null,
    updated_at timestamp    not null
);