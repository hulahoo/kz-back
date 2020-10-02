drop view if exists TSADV_ACTIVITY_TASK_VIEW;

create or replace view tsadv_activity_task_view as
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
  ),
       process_name AS (
         SELECT 'salaryRequestApproval'::text  AS name,
                'Заявка на изменение ЗП'::text AS processnameru,
                'Salary change request'::text  AS processnameen
         UNION
         SELECT 'assignmentRequest'::text AS name,
                'Заявка на перевод'::text AS processnameru,
                'Transfer request'::text  AS processnameen
         UNION
         SELECT 'assignmentSalaryRequest'::text             AS name,
                'Заявка на перевод с изменением ЗП'::text   AS processnameru,
                'Transfer with salary change request'::text AS processnameen
         UNION
         SELECT 'temporaryTranslationRequest'::text AS name,
                'Заявка на временный перевод'::text AS processnameru,
                'Temporary transfer request'::text  AS processnameen
         UNION
         SELECT 'absenceRequest'::text AS name,
                'Заявка на '::text     AS processnameru,
                ' request'::text       AS processnameen
         UNION
         SELECT 'positionRequest'::text AS name,
                'Заявка на '::text      AS processnameru,
                ' request'::text        AS processnameen
       )
  SELECT a.id,
         a.version,
         a.create_ts,
         a.created_by,
         a.update_ts,
         a.updated_by,
         a.delete_ts,
         a.deleted_by,
         a.id                                                                                                  AS activity_id,
         (t.request_number)::text                                                                              AS order_code,
         COALESCE((pn.processnameru || COALESCE(lower((abstype.lang_value1)::text), ((
           CASE
             WHEN ((t4.request_type)::text = 'NEW'::text) THEN 'создание ШЕ'::text
             WHEN ((t4.request_type)::text = 'CHANGE'::text) THEN 'изменение ШЕ'::text
             WHEN ((t4.request_type)::text = 'CLOSE'::text) THEN 'закрытые ШЕ'::text
             ELSE NULL::text
             END)::character varying)::text, ((''::text)::character varying)::text)),
                  concat(bpd.name, '_ru'))                                                                     AS process_ru,
         COALESCE(((COALESCE(abstype.lang_value3, (
           CASE
             WHEN ((t4.request_type)::text = 'NEW'::text) THEN 'Create position'::text
             WHEN ((t4.request_type)::text = 'CHANGE'::text) THEN 'Change position'::text
             WHEN ((t4.request_type)::text = 'CLOSE'::text) THEN 'Close position'::text
             ELSE NULL::text
             END)::character varying, (''::text)::character varying))::text || pn.processnameen),
                  concat(bpd.name, '_en'))                                                                     AS process_en,
         t.person_group_id,
         bpi.create_ts                                                                                         AS order_date,
         a.status,
         COALESCE(t3.date_from, t4.effective_date, t5.start_date, t6.date_from, t7.date_from,
                  t8.start_date)                                                                               AS start_date,
         (COALESCE(t3.date_from, t4.effective_date, t5.start_date, t6.date_from, t7.date_from, t8.start_date) -
          COALESCE(ed.value, 0))                                                                               AS expiry_date,
         ((a.status = 10) AND
          ((COALESCE(t3.date_from, t4.effective_date, t5.start_date, t6.date_from, t7.date_from, t8.start_date) -
            COALESCE(ed.value, 0)) <
           ('now'::text)::date))                                                                               AS is_expired_task,
         CASE
           WHEN ((t4.request_type)::text = 'NEW'::text) THEN t4.job_name_lang1
           ELSE COALESCE(pos.position_full_name_lang1,
                         (concat(person.last_name, ' ', person.first_name, ' ', person.middle_name))::character varying)
           END                                                                                                 AS detail_ru,
         CASE
           WHEN ((t4.request_type)::text = 'NEW'::text) THEN t4.job_name_lang3
           ELSE COALESCE(pos.position_full_name_lang3,
                         (concat(person.last_name_latin, ' ', person.first_name_latin, ' ',
                                 person.middle_name_latin))::character varying)
           END                                                                                                 AS detail_en
  FROM ((((((((((((((((((uactivity_activity a
    JOIN uactivity_activity_type tp ON (((a.type_id = tp.id) AND ((tp.code)::text <> 'NOTIFICATION'::text) AND
                                         (tp.delete_ts IS NULL))))
    JOIN bpm_proc_instance bpi ON ((bpi.entity_id = a.reference_id)))
    JOIN bpm_proc_definition bpd ON ((bpd.id = bpi.proc_definition_id)))
    JOIN tsadv_bpm_proc_instance_vw t ON ((t.bpm_proc_instance_id = bpi.id)))
    LEFT JOIN tsadv_absence_request t3 ON (((t3.id = a.reference_id) AND (t3.delete_ts IS NULL))))
    LEFT JOIN tsadv_dic_absence_type abstype ON (((abstype.id = t3.type_id) AND (abstype.delete_ts IS NULL))))
    LEFT JOIN tsadv_position_change_request t4 ON (((t4.id = a.reference_id) AND (t4.delete_ts IS NULL))))
    LEFT JOIN tsadv_dic_cost_center cc ON (((cc.id = t4.cost_center_id) AND (cc.delete_ts IS NULL))))
    LEFT JOIN base_position pos ON (((pos.group_id = t4.position_group_id) AND (pos.delete_ts IS NULL) AND
                                     (t4.effective_date >= pos.start_date) AND (t4.effective_date <= pos.end_date))))
    LEFT JOIN tsadv_salary_request t5 ON (((t5.id = a.reference_id) AND (t5.delete_ts IS NULL))))
    LEFT JOIN base_assignment ass ON (((ass.group_id = COALESCE(t3.assignment_group_id, t5.assignment_group_id)) AND
                                       (ass.delete_ts IS NULL) AND
                                       (COALESCE(t3.request_date, t5.start_date) >= ass.start_date) AND
                                       (COALESCE(t3.request_date, t5.start_date) <= ass.end_date) AND
                                       (ass.primary_flag IS TRUE))))
    LEFT JOIN tsadv_assignment_request t6 ON (((t6.id = a.reference_id) AND (t6.delete_ts IS NULL))))
    LEFT JOIN tsadv_assignment_salary_request t7 ON (((t7.id = a.reference_id) AND (t7.delete_ts IS NULL))))
    LEFT JOIN tsadv_temporary_translation_request t8 ON (((t8.id = a.reference_id) AND (t8.delete_ts IS NULL))))
    LEFT JOIN base_person person ON (((person.group_id =
                                       COALESCE(ass.person_group_id, t6.person_group_id, t7.person_group_id,
                                                t8.person_group_id)) AND
                                      (COALESCE(t3.date_from, t4.effective_date, t5.start_date, t6.date_from,
                                                t7.date_from, t8.start_date) >= person.start_date) AND
                                      (COALESCE(t3.date_from, t4.effective_date, t5.start_date, t6.date_from,
                                                t7.date_from, t8.start_date) <= person.end_date) AND
                                      (person.delete_ts IS NULL))))
    LEFT JOIN expiry_date ed ON ((ed.name = (bpd.name)::text)))
    LEFT JOIN process_name pn ON ((pn.name = (bpd.name)::text)))
         LEFT JOIN tsadv_bpm_request_message m ON (((m.activity_id = a.id) AND (m.delete_ts IS NULL))))
  WHERE ((a.delete_ts IS NULL) AND (m.* IS NULL));

alter table tsadv_activity_task_view owner to tal;

