-- alter table
create view aa_job_vw as
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
       o.group_id job_group_id,
       o.job_name_lang1 as job_name_ru,
       o.job_name_lang2 as job_name_kz,
       o.job_name_lang3 as job_name_en,
       max(o.start_date) over (partition by group_id) as max_start_date,
       o.employee_category_id
from tsadv_job o
where o.delete_ts is null;
