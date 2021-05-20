
-- hierarchy functions
-- the last version
create or replace function get_child_hierarchy_element(hierarchy_id uuid, parent_group_id uuid, system_date date)
  returns TABLE
          (
            id       uuid,
            hasChild boolean
          )
  language plpgsql
as
$$
Begin
  return query execute
    'with hierarchy as (
  select el.*
  from base_hierarchy_element el
         left join get_hierarchy_exception($3) e
                   on e.id = el.id
  where e is null
    and el.hierarchy_id = $1
    and $3 between el.start_date and el.end_date
    and el.delete_ts is null
),
     hierarchy_child as (
       select hierarchy.parent_group_id
       from hierarchy
       group by hierarchy.parent_group_id
     )
select el.id, child.parent_group_id is not null as hasChild
from hierarchy el
       left join hierarchy_child child on child.parent_group_id = el.group_id
where case when $2 is null then el.parent_group_id is null else el.parent_group_id = $2 end'
    using hierarchy_id,parent_group_id,system_date;
Exception
  When Others Then
    RAISE NOTICE 'Error: %', 'Error get_child_hierarchy_element';
    RAISE;
END;
$$;

alter function get_child_hierarchy_element(uuid,uuid,date) owner to tal;

create or replace function search_hierarchy_element(hierarchy_id uuid, search_text text, system_date date)
  returns TABLE
          (
            id          uuid,
            group_id    uuid,
            parent_path text,
            child_path  text
          )
  language plpgsql
as
$$
Begin
  return query execute
    'with recursive hierarchy_parent as (
  with hierarchy as (
    select el.*
    from base_hierarchy_element el
           left join get_hierarchy_exception($3) e
                     on e.id = el.id
      where e is null
           and el.hierarchy_id = $1
           and $3 between el.start_date and el.end_date
           and el.delete_ts is null
  )
  select hierarchy.*,
         hierarchy.id::text as path
  from hierarchy
    where
       hierarchy.parent_group_id is null
    union
    select
       h.*,
       hp.path || ''|'' || h.id::text as path
    from
       hierarchy_parent hp
         join hierarchy h
              on h.parent_group_id = hp.group_id
),
               hierarchy as (
                 select el.*
                 from base_hierarchy_element el
                        left join get_hierarchy_exception($3) e
                                  on e.id = el.id
                   where e is null
                        and el.hierarchy_id = $1
                        and $3 between el.start_date and el.end_date
                        and el.delete_ts is null
               ),
               children as (
                 select hierarchy.parent_group_id, string_agg(hierarchy.id::text, ''|'') as child_ids
                 from hierarchy
                   group by hierarchy.parent_group_id
               )
select el.id, el.group_id, el.path, c.child_ids
from hierarchy_parent el
       left join children c on c.parent_group_id = el.group_id
       left join base_position p
                 on p.group_id = el.position_group_id
                   and $3 between p.start_date and p.end_date
                   and p.delete_ts is null
       left join base_organization o
                 on o.group_id = el.organization_group_id
                   and $3 between o.start_date and o.end_date
                   and o.delete_ts is null
  where o.id is not null
       and ( lower(o.organization_name_lang1) ~ $2
or lower(o.organization_name_lang2) ~ $2
or lower(o.organization_name_lang3) ~ $2)
   or p.id is not null
and (lower(p.position_full_name_lang1) ~ $2
  or lower(p.position_full_name_lang2) ~ $2
  or lower(p.position_full_name_lang3) ~ $2)'
    using hierarchy_id,lower(search_text),system_date;
Exception
  When Others Then
    RAISE NOTICE 'Error: %', 'Error search_hierarchy_element';
    RAISE;
END;
$$;

alter function search_hierarchy_element(uuid,text,date) owner to tal;


create or replace function get_hierarchy_exception(system_date date) returns TABLE(id uuid)
	language plpgsql
as $$
Begin
  return query execute
    ' with recursive org as (
                    with postOrg as (
                      select o.group_id                                 as organization_group_ext_id,
                             string_agg(tdps.code, '' '') is not null and string_agg(tdps.code, '' '') like ''%ACTIVE%'' as is_active
                      from base_organization o
                             left join base_position p
                                       on o.group_id = p.organization_group_ext_id
                                         and $1 between p.start_date and p.end_date
                                         and p.delete_ts is null
                             left join tsadv_dic_position_status tdps
                                       on p.position_status_id = tdps.id
                                         and tdps.delete_ts is null
                      where $1 between o.start_date and o.end_date
                        and o.delete_ts is null
                      group by o.group_id
                      )
                      select el.id, el.group_id, el.organization_group_id, p.is_active as is_has_active_child
                      from postOrg p
                             join base_hierarchy_element el
                                  on p.organization_group_ext_id = el.organization_group_id
                                    and $1 between el.start_date and el.end_date
                                    and el.delete_ts is null
                                    and el.element_type = 1
                      where p.is_active is false
                      union
                      select el.id, el.group_id, o.organization_group_id, (o.is_has_active_child or p.is_active) as is_has_active_child
                      from org o
                             join base_hierarchy_element el
                                  on el.parent_group_id = o.group_id
                                    and $1 between el.start_date and el.end_date
                                    and el.element_type = 1
                                    and el.delete_ts is null
                             join postOrg p
                                  on p.organization_group_ext_id = el.organization_group_id
                  ),
                                 closedOrg as (
                                   select o.group_id                                 as organization_group_ext_id,
                                          string_agg(tdps.code, '' '') is not null and string_agg(tdps.code, '' '') like ''%ACTIVE%'' as is_active
                                   from base_organization o
                                          left join base_position p
                                                    on o.group_id = p.organization_group_ext_id
                                                      and $1 between p.start_date and p.end_date
                                                      and p.delete_ts is null
                                          left join tsadv_dic_position_status tdps
                                                    on p.position_status_id = tdps.id
                                                      and tdps.delete_ts is null
                                   where $1 between o.start_date and o.end_date
                                     and o.delete_ts is null
                                   group by o.group_id
                                   having string_agg(tdps.code, '' '') like ''%ACTIVE%'' is false
											or string_agg(tdps.code, '' '') is null
                                 ),
                                 closedOrgWithChild as (
                                   select o.organization_group_id
                                   from org o
                                   where o.is_has_active_child = true
                                   group by o.organization_group_id
                                 )
                  select el.id
                  from closedOrg co
                         join base_hierarchy_element el
                             on el.organization_group_id = co.organization_group_ext_id
                             and el.delete_ts is null
                             and $1 between el.start_date and el.end_date
                         left join closedOrgWithChild cowc
                                   on co.organization_group_ext_id = cowc.organization_group_id
                  where cowc is null '
    using system_date;
Exception
  When Others Then
    RAISE NOTICE 'Error: %', 'Error get_hierarchy_exception';
    RAISE;
END;
$$;

alter function get_hierarchy_exception(date) owner to tal;

CREATE OR REPLACE FUNCTION sys_config_value(character varying)
 RETURNS character varying
 LANGUAGE sql
 IMMUTABLE STRICT
AS $function$
select s.value_::varchar from SYS_CONFIG s where s.name = $1;
$function$;


