create or replace view aa_position_vw as
---представление для справочника штатных единиц
select bp.id,
       bp."version",
       bp.create_ts,
       bp.created_by,
       bp.update_ts,
       bp.updated_by,
       bp.delete_ts,
       bp.deleted_by,
       bp.position_full_name_lang1                        position_name_ru,
       bp.position_full_name_lang2                        position_name_kz,
       bp.position_full_name_lang3                        position_name_en,
       bp.start_date,
       bp.end_date,
       max(bp.start_date) over (partition by bp.group_id) max_start_date,
       bp.group_id                                        position_group_id,
       bp.organization_group_ext_id                       organization_group_id,
       bp.grade_group_id,
       bp.fte,
       cc.code                                            cost_center
from base_position bp
       join tsadv_dic_position_status dps
            on dps.id = bp.position_status_id
              and dps.code = 'ACTIVE'
       left join tsadv_dic_cost_center cc
                 on cc.id = bp.cost_center_id
where bp.delete_ts is null
  and bp.fte > 0