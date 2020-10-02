drop view if exists tal.tsadv_bpm_proc_instance_vw cascade;

CREATE OR REPLACE VIEW tal.tsadv_bpm_proc_instance_vw
AS SELECT bpi.id,
    bpi.version,
    bpi.create_ts,
    bpi.created_by,
    NULL::timestamp without time zone AS update_ts,
    NULL::unknown AS updated_by,
    NULL::timestamp without time zone AS delete_ts,
    NULL::unknown AS deleted_by,
    bpi.id AS bpm_proc_instance_id,
        CASE
            WHEN bpi.entity_name::text = 'aa$SalaryRequestAA'::text THEN sr.person_group_id
            WHEN bpi.entity_name::text = 'aa$AssignmentSalaryRequestAA'::text THEN asr.person_group_id
            WHEN bpi.entity_name::text = 'tsadv$AssignmentRequest'::text THEN ar.person_group_id
            WHEN bpi.entity_name::text = 'tsadv$TemporaryTranslationRequest'::text THEN ttr.person_group_id
            ELSE up.person_group_id
        END AS person_group_id,
        CASE
            WHEN bpi.entity_name::text = 'aa$SalaryRequestAA'::text THEN sr.request_number
            WHEN bpi.entity_name::text = 'aa$AssignmentSalaryRequestAA'::text THEN asr.request_number
            WHEN bpi.entity_name::text = 'tsadv$AssignmentRequest'::text THEN ar.request_number
            WHEN bpi.entity_name::text = 'tsadv$TemporaryTranslationRequest'::text THEN ttr.request_number
            WHEN bpi.entity_name::text = 'aa$AbsenceRequestAA'::text THEN COALESCE(abr.request_number, 0::bigint)
            WHEN bpi.entity_name::text = 'tsadv$PersonalDataRequest'::text THEN COALESCE(pdr.request_number, 0::bigint)
            WHEN bpi.entity_name::text = 'tsadv$PositionChangeRequest'::text THEN COALESCE(pcr.request_number, 0::bigint)
            WHEN bpi.entity_name::text = 'tsadv$AddressRequest'::text THEN COALESCE(addr.request_number, 0::bigint)
            ELSE '0'::bigint
        END AS request_number,
    bpi.entity_name
   FROM bpm_proc_instance bpi
     JOIN tsadv_user_ext_person_group up ON up.user_ext_id = bpi.started_by_id AND up.delete_ts IS NULL
     LEFT JOIN ( SELECT a.person_group_id,
            sr_1.id,
            sr_1.request_number
           FROM tsadv_salary_request sr_1
             JOIN base_assignment a ON a.group_id = sr_1.assignment_group_id AND a.delete_ts IS NULL AND sr_1.start_date >= a.start_date AND sr_1.start_date <= a.end_date) sr ON sr.id = bpi.entity_id
     LEFT JOIN tsadv_assignment_salary_request asr ON asr.id = bpi.entity_id
     LEFT JOIN tsadv_assignment_request ar ON ar.id = bpi.entity_id
     LEFT JOIN tsadv_temporary_translation_request ttr ON ttr.id = bpi.entity_id
     LEFT JOIN tsadv_absence_request abr ON abr.id = bpi.entity_id
     LEFT JOIN tsadv_personal_data_request pdr ON pdr.id = bpi.entity_id
     LEFT JOIN tsadv_position_change_request pcr ON pcr.id = bpi.entity_id
     LEFT JOIN tsadv_address_request addr ON addr.id = bpi.entity_id
  WHERE bpi.delete_ts IS NULL;