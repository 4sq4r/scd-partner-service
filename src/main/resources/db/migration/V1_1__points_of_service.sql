create table if not exists points_of_service
(
    id         bigserial primary key,
    address    varchar     not null,
    phone      varchar(13) not null unique,
    company_id bigint      not null,
    created_at timestamp   not null default now(),
    updated_at timestamp   not null default now(),

    constraint fk_company foreign key (company_id) references companies (id)
);