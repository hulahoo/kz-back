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