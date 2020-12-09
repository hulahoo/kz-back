update base_hierarchy_element
set parent_group_id = el.group_id
from base_hierarchy_element el
where el.id = base_hierarchy_element.parent_id;