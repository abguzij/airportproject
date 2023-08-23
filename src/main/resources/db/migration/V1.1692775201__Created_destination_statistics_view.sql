create view destination_statistics_view as
select destination, count(*)
from public.flights
group by destination;