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