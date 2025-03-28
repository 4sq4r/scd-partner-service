create table if not exists media_files
(
    id            bigserial primary key,
    user_id       bigint references users (id)       not null,
    media_file_id bigint references media_files (id) not null,
    created_at    timestamp                          not null,
    updated_at    timestamp                          not null
);