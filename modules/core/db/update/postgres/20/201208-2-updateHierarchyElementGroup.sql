insert into tsadv_hierarchy_element_group (id, version, create_ts, created_by
)
select newid(), 1, current_date, 'admin'
from base_hierarchy_element el
where el.position_group_id is not null
group by el.position_group_id;