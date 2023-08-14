insert into public.application_users (username, user_password, full_name, registered_at, position_id)
values (
        'admin',
        '$2a$08$aV2e9d/hcmW57VagrFIlvuG2Kuvk9VLmGHECyG93i5sKAtHdY5VEq',
        'Ivan Petrov',
        now(),
        1
);