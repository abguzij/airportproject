insert into public.user_positions
    (position_title)
values
    ('SYSTEM_ADMINISTRATOR'),
    ('AIRPORT_MANAGER'),
    ('CHIEF_DISPATCHER'),
    ('DISPATCHER'),
    ('CHIEF_ENGINEER'),
    ('ENGINEER'),
    ('PILOT'),
    ('CHIEF_STEWARD'),
    ('STEWARD');

insert into public.user_roles
    (role_title, position_id)
values
    ('CLIENT', null),
    ('ADMIN', 1),
    ('MANAGER', 2),
    ('CHIEF_DISPATCHER', 3),
    ('DISPATCHER', 4),
    ('CHIEF_ENGINEER', 5),
    ('ENGINEER', 6),
    ('PILOT', 7),
    ('CHIEF_STEWARD', 8),
    ('STEWARD', 9);