alter table users
    add address varchar(255);

alter table users
    add first_name varchar(100);

alter table users
    add last_name varchar(100);

alter table users
    add phone_number bigint unique ;