-- alter table
create view aa_grade_vw as
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
       o.group_id grade_group_id,
       o.grade_name,
       max(o.start_date) over (partition by group_id) as max_start_date
from tsadv_grade o
where o.delete_ts is null;
