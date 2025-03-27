create table if not exists jobs
(
    id          bigserial primary key,
    name        varchar(255)   not null,
    description varchar(500)   not null,
    price       numeric(19, 2) not null default 0,
    duration    integer        not null default 0,
    user_id     bigint         not null references users (id),
    created_at  timestamp      not null,
    updated_at  timestamp      not null
);

alter table jobs
    add constraint unq_jobs_on_user_id_and_name unique (user_id, name);