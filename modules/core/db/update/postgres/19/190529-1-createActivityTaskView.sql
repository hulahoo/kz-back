drop view if exists tsadv_activity_task_view;

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
    a.status
   FROM uactivity_activity a
     JOIN bpm_proc_instance bpi ON bpi.entity_id = a.reference_id
     JOIN bpm_proc_definition bpd ON bpd.id = bpi.proc_definition_id
     JOIN tsadv_bpm_proc_instance_vw t ON t.bpm_proc_instance_id = bpi.id;