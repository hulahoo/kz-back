create view tsadv_bpm_proc_instance_vw as
  WITH process_name AS (
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
  SELECT bpi.id,
         bpi.version,
         bpi.create_ts,
         bpi.created_by,
         bpi.update_ts,
         bpi.updated_by,
         bpi.delete_ts,
         bpi.deleted_by,
         bpi.start_date,
         bpi.end_date,
         bpi.proc_definition_id,
         COALESCE(sra.person_group_id, asr.person_group_id, ar.person_group_id, ttr.person_group_id,
                  up.person_group_id)                                                                            AS person_group_id,
         COALESCE(sr.request_number, asr.request_number, ar.request_number, ttr.request_number, abr.request_number,
                  pdr.request_number, pcr.request_number, addr.request_number,
                  (0)::bigint)                                                                                   AS request_number,
         bpi.entity_name,
         bpi.entity_id,
         bpi.started_by_id,
         up.person_group_id                                                                                      AS STARTED_BY_PERSON_GROUP_ID,
         bpi.active,
         bpi.cancelled,
         bpa.user_id                                                                                             AS current_approver_id,
         COALESCE((pn.processnameru || COALESCE(lower((abrt.lang_value1)::text), ((
           CASE
             WHEN ((pcr.request_type)::text = 'NEW'::text) THEN 'создание ШЕ'::text
             WHEN ((pcr.request_type)::text = 'CHANGE'::text) THEN 'изменение ШЕ'::text
             WHEN ((pcr.request_type)::text = 'CLOSE'::text) THEN 'закрытые ШЕ'::text
             ELSE NULL::text
             END)::character varying)::text, ((''::text)::character varying)::text)),
                  concat(bpd.name, '_ru'))                                                                       AS process_ru,
         COALESCE(((COALESCE(abrt.lang_value3, (
           CASE
             WHEN ((pcr.request_type)::text = 'NEW'::text) THEN 'Create position'::text
             WHEN ((pcr.request_type)::text = 'CHANGE'::text) THEN 'Change position'::text
             WHEN ((pcr.request_type)::text = 'CLOSE'::text) THEN 'Close position'::text
             ELSE NULL::text
             END)::character varying, (''::text)::character varying))::text || pn.processnameen),
                  concat(bpd.name, '_en'))                                                                       AS process_en,
         COALESCE(abr.date_from, pcr.effective_date, sr.start_date, ar.date_from, asr.date_from,
                  ttr.start_date)                                                                                AS effective_date,
         CASE
           WHEN ((pcr.request_type)::text = 'NEW'::text) THEN pcr.job_name_lang1
           ELSE COALESCE(pos.position_full_name_lang1,
                         (concat(person.last_name, ' ', person.first_name, ' ', person.middle_name))::character varying)
           END                                                                                                   AS detail_ru,
         CASE
           WHEN ((pcr.request_type)::text = 'NEW'::text) THEN pcr.job_name_lang3
           ELSE COALESCE(pos.position_full_name_lang3,
                         (concat(person.last_name_latin, ' ', person.first_name_latin, ' ',
                                 person.middle_name_latin))::character varying)
           END                                                                                                   AS detail_en
  FROM (((((((((((((((((bpm_proc_instance bpi
    JOIN bpm_proc_definition bpd ON ((bpd.id = bpi.proc_definition_id)))
    LEFT JOIN tsadv_user_ext_person_group up ON (((up.user_ext_id = bpi.started_by_id) AND (up.delete_ts IS NULL))))
    LEFT JOIN tsadv_salary_request sr ON (((sr.id = bpi.entity_id) AND (sr.delete_ts IS NULL))))
    LEFT JOIN base_assignment sra ON (((sra.group_id = sr.assignment_group_id) AND (sra.delete_ts IS NULL) AND
                                       ((sr.start_date >= sra.start_date) AND (sr.start_date <= sra.end_date)))))
    LEFT JOIN tsadv_assignment_salary_request asr ON (((asr.id = bpi.entity_id) AND (asr.delete_ts IS NULL))))
    LEFT JOIN tsadv_assignment_request ar ON (((ar.id = bpi.entity_id) AND (ar.delete_ts IS NULL))))
    LEFT JOIN tsadv_temporary_translation_request ttr ON (((ttr.id = bpi.entity_id) AND (ttr.delete_ts IS NULL))))
    LEFT JOIN tsadv_absence_request abr ON (((abr.id = bpi.entity_id) AND (abr.delete_ts IS NULL))))
    LEFT JOIN tsadv_dic_absence_type abrt ON (((abrt.id = abr.type_id) AND (abrt.delete_ts IS NULL))))
    LEFT JOIN tsadv_personal_data_request pdr ON (((pdr.id = bpi.entity_id) AND (pdr.delete_ts IS NULL))))
    LEFT JOIN tsadv_position_change_request pcr ON (((pcr.id = bpi.entity_id) AND (pcr.delete_ts IS NULL))))
    LEFT JOIN base_position pos ON (((pos.group_id = pcr.position_group_id) AND (pos.delete_ts IS NULL) AND
                                     (pcr.effective_date >= pos.start_date) AND (pcr.effective_date <= pos.end_date))))
    LEFT JOIN tsadv_address_request addr ON (((addr.id = bpi.entity_id) AND (addr.delete_ts IS NULL))))
    LEFT JOIN base_person person ON (((person.group_id =
                                       COALESCE(sra.person_group_id, asr.person_group_id, ar.person_group_id,
                                                ttr.person_group_id, up.person_group_id)) AND
                                      ((COALESCE(abr.date_from, pcr.effective_date, sr.start_date, ar.date_from,
                                                 asr.date_from, ttr.start_date) >= person.start_date) AND
                                       (COALESCE(abr.date_from, pcr.effective_date, sr.start_date, ar.date_from,
                                                 asr.date_from, ttr.start_date) <= person.end_date)) AND
                                      (person.delete_ts IS NULL))))
    LEFT JOIN bpm_proc_task bpt ON (((bpt.proc_instance_id = bpi.id) AND (bpt.delete_ts IS NULL) AND
                                     (bpt.end_date IS NULL))))
    LEFT JOIN bpm_proc_actor bpa ON (((bpt.proc_actor_id = bpa.id) AND (bpa.delete_ts IS NULL))))
         LEFT JOIN process_name pn ON ((pn.name = (bpd.name)::text)))
  WHERE (bpi.delete_ts IS NULL);

alter table tsadv_bpm_proc_instance_vw
  owner to tal;