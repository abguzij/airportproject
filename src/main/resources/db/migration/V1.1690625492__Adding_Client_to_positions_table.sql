insert into user_positions (position_title)
values ('CLIENT');

update public.user_roles
set position_id = 10
where role_title = 'CLIENT';