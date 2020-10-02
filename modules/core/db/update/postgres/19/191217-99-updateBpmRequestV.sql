create or replace view tsadv_bpm_proc_instance_vw as
SELECT bpi.id,
       bpi.version,
       bpi.create_ts,
       bpi.created_by,
       NULL::timestamp without time zone                                                                              AS update_ts,
       NULL::unknown                                                                                                  AS updated_by,
       NULL::timestamp without time zone                                                                              AS delete_ts,
       NULL::unknown                                                                                                  AS deleted_by,
       bpi.id                                                                                                         AS bpm_proc_instance_id,
       COALESCE(sr.person_group_id, asr.person_group_id, ar.person_group_id, ttr.person_group_id,
                up.person_group_id)                                                                                   AS person_group_id,
       COALESCE(sr.request_number, asr.request_number, ar.request_number, ttr.request_number, abr.request_number,
                pdr.request_number, pcr.request_number, addr.request_number,
                (0)::bigint)                                                                                          AS request_number,
       bpi.entity_name
FROM (((((((((bpm_proc_instance bpi
  JOIN tsadv_user_ext_person_group up ON (((up.user_ext_id = bpi.started_by_id) AND (up.delete_ts IS NULL))))
  LEFT JOIN (SELECT a.person_group_id,
                    sr_1.id,
                    sr_1.request_number
             FROM (tsadv_salary_request sr_1
                    JOIN base_assignment a ON (((a.group_id = sr_1.assignment_group_id) AND (a.delete_ts IS NULL) AND
                                                (sr_1.start_date >= a.start_date) AND
                                                (sr_1.start_date <= a.end_date))))) sr ON ((sr.id = bpi.entity_id)))
  LEFT JOIN tsadv_assignment_salary_request asr ON ((asr.id = bpi.entity_id)))
  LEFT JOIN tsadv_assignment_request ar ON ((ar.id = bpi.entity_id)))
  LEFT JOIN tsadv_temporary_translation_request ttr ON ((ttr.id = bpi.entity_id)))
  LEFT JOIN tsadv_absence_request abr ON ((abr.id = bpi.entity_id)))
  LEFT JOIN tsadv_personal_data_request pdr ON ((pdr.id = bpi.entity_id)))
  LEFT JOIN tsadv_position_change_request pcr ON ((pcr.id = bpi.entity_id)))
       LEFT JOIN tsadv_address_request addr ON ((addr.id = bpi.entity_id)))
WHERE (bpi.delete_ts IS NULL);

alter table tsadv_bpm_proc_instance_vw owner to tal;

