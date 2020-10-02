CREATE OR REPLACE VIEW tal.tsadv_activity_task_view
AS WITH expiry_date AS (
         SELECT COALESCE(c.value::integer, 0) AS value,
            n.name
           FROM sys_config c
             JOIN ( SELECT 'salaryRequestApproval'::text AS name
                UNION
                 SELECT 'assignmentRequest'::text AS name
                UNION
                 SELECT 'assignmentSalaryRequest'::text AS name
                UNION
                 SELECT 'temporaryTranslationRequest'::text AS name) n ON 1 = 1
          WHERE c.name::text = 'tsadv.bpm.transfer.beforeEnding'::text
        UNION
         SELECT COALESCE(c.value::integer, 0) AS value,
            'positionRequest'::text AS name
           FROM sys_config c
          WHERE c.name::text = 'tsadv.bpm.position.beforeEnding'::text
        UNION
         SELECT 7 AS value,
            'absenceRequest'::text AS name
        )
 SELECT a.id,
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
    COALESCE(t3.date_from, t4.effective_date, t5.start_date, t6.date_from, t7.date_from, t8.start_date) AS start_date,
    COALESCE(t3.date_from, t4.effective_date, t5.start_date, t6.date_from, t7.date_from, t8.start_date) - COALESCE(ed.value, 0) AS expiry_date,
    a.status = 10 AND (COALESCE(t3.date_from, t4.effective_date, t5.start_date, t6.date_from, t7.date_from, t8.start_date) - COALESCE(ed.value, 0)) < 'now'::text::date AS is_expired_task
   FROM uactivity_activity a
     JOIN uactivity_activity_type tp ON a.type_id = tp.id AND tp.code::text <> 'NOTIFICATION'::text AND tp.delete_ts IS NULL
     JOIN bpm_proc_instance bpi ON bpi.entity_id = a.reference_id
     JOIN bpm_proc_definition bpd ON bpd.id = bpi.proc_definition_id
     JOIN tsadv_bpm_proc_instance_vw t ON t.bpm_proc_instance_id = bpi.id
     LEFT JOIN tsadv_absence_request t3 ON t3.id = a.reference_id AND t3.delete_ts IS NULL
     LEFT JOIN tsadv_position_change_request t4 ON t4.id = a.reference_id AND t4.delete_ts IS NULL
     LEFT JOIN tsadv_salary_request t5 ON t5.id = a.reference_id AND t5.delete_ts IS NULL
     LEFT JOIN tsadv_assignment_request t6 ON t6.id = a.reference_id AND t6.delete_ts IS NULL
     LEFT JOIN tsadv_assignment_salary_request t7 ON t7.id = a.reference_id AND t7.delete_ts IS NULL
     LEFT JOIN tsadv_temporary_translation_request t8 ON t8.id = a.reference_id AND t8.delete_ts IS NULL
     LEFT JOIN expiry_date ed ON ed.name = bpd.name::text
  WHERE a.delete_ts IS NULL;

-- Permissions

ALTER TABLE tal.tsadv_activity_task_view OWNER TO tal;
GRANT ALL ON TABLE tal.tsadv_activity_task_view TO tal;
