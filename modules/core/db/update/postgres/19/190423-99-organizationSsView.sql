create or replace view aa_organization_vw as
---представление для справочника штатных единиц
select * from (
select o.id, 
o."version",
o.create_ts,
o.created_by,
o.update_ts,
o.updated_by,
o.delete_ts,
o.deleted_by,
o.start_date,
o.end_date,
max(o.start_date) over (partition by o.group_id) max_start_date,
o.group_id organization_group_id,
o.organization_name_lang1 as organization_name_ru,
o.organization_name_lang2 as organization_name_kz,
o.organization_name_lang3 as organization_name_en,
cc.code cost_center
from base_organization o 
left join tsadv_dic_cost_center cc
on cc.id = o.cost_center_id
where o.delete_ts is null 
) t 
where t.start_date = t.max_start_date
