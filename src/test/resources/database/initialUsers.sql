insert into users (id,email,password_hash,first_name,last_name,activated,created_by,created_date,last_modified_by)
values(1,'system@localhost','$2a$10$VEjxo0jq2YG9Rbk2HmX9S.k1uZBGYUHdUcid3g/vfiEl7lwWgOH/K','System','System',true,'system','2018-06-20 10:00:00','system');

insert into authority (name) values ('ROLE_SYSTEM');
insert into authority (name) values ('ROLE_ADMIN');

insert into users_authority (user_id, authority_name) values (1,'ROLE_SYSTEM');
insert into users_authority (user_id, authority_name) values (1,'ROLE_ADMIN');
