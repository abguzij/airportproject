create table if not exists public.user_positions(
    id bigserial primary key,
    position_title varchar not null
);

create table if not exists public.user_roles(
    id bigserial primary key,
    role_title varchar not null,
    position_id bigint references public.user_positions(id)
);

create table if not exists public.application_users(
    id bigserial primary key,
    username varchar not null,
    user_password varchar not null,
    full_name varchar not null,
    registered_at timestamp not null,
    is_enabled boolean not null default true,
    position_id bigint references public.user_positions(id)
);

create table if not exists public.m2m_users_roles(
    user_id bigint references public.application_users(id),
    role_id bigint references public.user_roles(id)
);

create table if not exists public.aircrafts(
    id bigserial primary key,
    aircraft_title varchar not null,
    aircraft_type varchar not null,
    aircraft_status varchar not null default 'NEEDS_INSPECTION',
    registered_at timestamp not null,
    serviced_by bigint references public.application_users(id)
);

create table if not exists public.aircraft_seats(
    id bigserial primary key,
    number_in_row integer not null,
    row_number integer not null,
    is_reserved boolean not null default false,
    aircraft_id bigint references public.aircrafts(id)
);

create table if not exists public.parts(
    id bigserial primary key,
    part_title varchar not null,
    aircraft_type varchar not null,
    part_type varchar not null,
    registered_at timestamp not null
);

create table if not exists public.m2m_aircrafts_parts(
    aircraft_id bigint references public.aircrafts(id),
    part_id bigint references public.parts(id)
);

create table if not exists public.part_inspections(
    id bigserial primary key,
    part_state varchar not null,
    registered_at timestamp not null,
    registered_by bigint references public.application_users(id),
    conducted_by bigint references public.application_users(id),
    part_id bigint references public.parts(id),
    aircraft_id bigint references public.aircrafts(id)
);

create table if not exists public.flights(
    id bigserial primary key,
    destination varchar not null,
    flight_status varchar not null,
    tickets_left integer not null default 0,
    registered_at timestamp not null,
    aircraft_id bigint references public.aircrafts(id)
);

create table if not exists public.users_flights(
    id bigserial primary key,
    user_status varchar not null,
    registered_at timestamp not null,
    user_id bigint references public.application_users(id),
    seat_id bigint references public.aircraft_seats(id),
    flight_id bigint references public.flights(id)
);

create table if not exists public.client_feedbacks(
    id bigserial primary key,
    feedback_text text not null,
    registered_at timestamp not null,
    client_id bigint references public.application_users(id),
    flight_id bigint references public.flights(id)
);




