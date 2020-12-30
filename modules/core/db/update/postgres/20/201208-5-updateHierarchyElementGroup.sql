with gg as (
  select row_number() over (order by a.id) as n, a.id
  from tsadv_hierarchy_element_group a
         left join base_hierarchy_element e on a.id = e.group_id
  where e is null
  group by a.id
),
     hh as (select row_number() over (order by el.organization_group_id) as n, el.organization_group_id
            from base_hierarchy_element el
            where el.organization_group_id is not null
            group by el.organization_group_id),
aa as (select el.id, gg.id as group_id
from base_hierarchy_element el
       join hh on hh.organization_group_id = el.organization_group_id
       join gg on gg.n = hh.n)
update base_hierarchy_element
set group_id = aa.group_id
from aa where aa.id = base_hierarchy_element.id;
