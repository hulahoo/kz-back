-- alter table
drop view TSADV_ACTIVITY_TASK_VIEW;

CREATE OR REPLACE VIEW tal.tsadv_activity_task_view
AS SELECT a.id,
    a.version,
    a.create_ts,
    a.created_by,
    a.update_ts,
    a.updated_by,
    a.delete_ts,
    a.deleted_by,
    a.id AS activity_id,
    t.request_number::text AS order_code,
    concat(bpd.name, '_ru') AS process_ru,
    concat(bpd.name, '_en') AS process_en,
    t.person_group_id,
    bpi.create_ts AS order_date,
    a.status,
    coalesce(t3.date_from, t4.effective_date, t5.start_date, t6.date_from, t7.date_from, t8.start_date) as start_date
   FROM uactivity_activity a
     JOIN bpm_proc_instance bpi ON bpi.entity_id = a.reference_id
     JOIN bpm_proc_definition bpd ON bpd.id = bpi.proc_definition_id
     JOIN tsadv_bpm_proc_instance_vw t ON t.bpm_proc_instance_id = bpi.id
     left join tsadv_absence_request t3 on t3.id = a.reference_id and t3.delete_ts is null
     left join tsadv_position_change_request t4 on t4.id = a.reference_id and t4.delete_ts is null
     left join tsadv_salary_request t5 on t5.id = a.reference_id and t5.delete_ts is null
     left join tsadv_assignment_request t6 on t6.id = a.reference_id and t6.delete_ts is null
     left join tsadv_assignment_salary_request t7 on t7.id = a.reference_id and t7.delete_ts is null
     left join tsadv_temporary_translation_request t8 on t8.id = a.reference_id and t8.delete_ts is null;

-- Permissions

ALTER TABLE tal.tsadv_activity_task_view OWNER TO tal;
GRANT ALL ON TABLE tal.tsadv_activity_task_view TO tal;
