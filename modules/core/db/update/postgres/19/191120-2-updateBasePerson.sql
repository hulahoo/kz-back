alter table BASE_PERSON add column if not exists FULL_NAME_CYRILLIC varchar(255) ;

update base_person p
   set full_name_cyrillic =
       p.last_name || ' ' || p.first_name ||
       case
          when p.middle_name is not null and p.middle_name <> ''
             then ' ' || p.middle_name
          else ''
       end
 where full_name_cyrillic is null;