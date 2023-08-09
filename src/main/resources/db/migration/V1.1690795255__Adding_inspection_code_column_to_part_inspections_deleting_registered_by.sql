alter table public.part_inspections drop column registered_by;

alter table public.part_inspections add column inspection_code bigint not null default 0;