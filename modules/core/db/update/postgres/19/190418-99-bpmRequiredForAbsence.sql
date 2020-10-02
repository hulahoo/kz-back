-- alter table
-- код функции bpmRequiredForAbsence:

CREATE OR REPLACE FUNCTION tal.bpmRequiredForAbsence(p_absence_request_id uuid)
RETURNS boolean 
LANGUAGE plpgsql
AS $function$ 
declare 
l_number integer default 0;
begin

select count(ar.id)
into l_number
from tsadv_absence_request ar 
join tsadv_dic_absence_type dat 
on dat.id = ar.type_id
and dat.code = 'UNPAID'
where ar.id = p_absence_request_id
and ar.absence_days>30;

if l_number>0 then return true; else return false; end if;

end;
$function$
