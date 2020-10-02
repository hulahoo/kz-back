create view tsadv_activity_task_view as
  WITH expiry_date AS (
    SELECT COALESCE((c.value)::integer, 0) AS value,
           n.name
    FROM (sys_config c
           JOIN (SELECT 'salaryRequestApproval'::text AS name
                 UNION
                 SELECT 'assignmentRequest'::text AS name
                 UNION
                 SELECT 'assignmentSalaryRequest'::text AS name
                 UNION
                 SELECT 'temporaryTranslationRequest'::text AS name) n ON ((1 = 1)))
    WHERE ((c.name)::text = 'tsadv.bpm.transfer.beforeEnding'::text)
    UNION
    SELECT COALESCE((c.value)::integer, 0) AS value,
           'positionRequest'::text         AS name
    FROM sys_config c
    WHERE ((c.name)::text = 'tsadv.bpm.position.beforeEnding'::text)
    UNION
    SELECT 7                      AS value,
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
         a.id                                                                                     AS activity_id,
         (t.request_number)::text                                                                 AS order_code,
         t.process_ru,
         t.process_en,
         t.person_group_id,
         t.create_ts                                                                              AS order_date,
         a.status,
         t.effective_date                                                                         AS start_date,
         (t.effective_date - COALESCE(ed.value, 0))                                               AS expiry_date,
         ((a.status = 10) AND ((t.effective_date - COALESCE(ed.value, 0)) < ('now'::text)::date)) AS is_expired_task,
         t.detail_ru,
         t.detail_en
  FROM (((((((((((uactivity_activity a
    JOIN uactivity_activity_type tp ON (((a.type_id = tp.id) AND ((tp.code)::text <> 'NOTIFICATION'::text) AND
                                         (tp.delete_ts IS NULL))))
    JOIN tsadv_bpm_proc_instance_vw t ON ((t.entity_id = a.reference_id)))
    JOIN bpm_proc_definition bpd ON ((bpd.id = t.proc_definition_id)))
    LEFT JOIN tsadv_absence_request t3 ON (((t3.id = a.reference_id) AND (t3.delete_ts IS NULL))))
    LEFT JOIN tsadv_position_change_request t4 ON (((t4.id = a.reference_id) AND (t4.delete_ts IS NULL))))
    LEFT JOIN tsadv_salary_request t5 ON (((t5.id = a.reference_id) AND (t5.delete_ts IS NULL))))
    LEFT JOIN tsadv_assignment_request t6 ON (((t6.id = a.reference_id) AND (t6.delete_ts IS NULL))))
    LEFT JOIN tsadv_assignment_salary_request t7 ON (((t7.id = a.reference_id) AND (t7.delete_ts IS NULL))))
    LEFT JOIN tsadv_temporary_translation_request t8 ON (((t8.id = a.reference_id) AND (t8.delete_ts IS NULL))))
    LEFT JOIN expiry_date ed ON ((ed.name = (bpd.name)::text)))
         LEFT JOIN tsadv_bpm_request_message m ON (((m.activity_id = a.id) AND (m.delete_ts IS NULL))))
  WHERE ((a.delete_ts IS NULL) AND (m.id IS NULL));

alter table tsadv_activity_task_view
  owner to tal;