alter table BASE_PERSON add column if not exists FULL_NAME_NUMBER_CYRILLIC varchar(255) ;

update base_person p
   set FULL_NAME_NUMBER_CYRILLIC =
       p.last_name || ' ' || p.first_name ||
       case
          when p.middle_name is not null and p.middle_name <> ''
             then ' ' || p.middle_name
          else ''
       end ||
       case
          when p.employee_number is not null and p.employee_number <> ''
             then ' (' || p.employee_number || ')'
          else ''
       end
 where FULL_NAME_NUMBER_CYRILLIC is null;