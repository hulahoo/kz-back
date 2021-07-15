drop view if exists tsadv_activity_task_view;

drop type if exists bproc_outcome;

create TYPE bproc_outcome AS ("outcomeId" varchar(50), "executionId" varchar(50), "taskDefinitionKey" varchar(100), "user" uuid, "date" bigint);

CREATE OR REPLACE VIEW tsadv_activity_task_view as

    with links as (
        select v.proc_inst_id_,
               rol_link.id as link_id,
               rol_link.hr_role_id,
               rol_link.bproc_user_task_code
        from act_ru_variable v
                 join act_ge_bytearray b on b.id_ = v.bytearray_id_
                 join json_array_elements_text(encode(b.bytes_, 'escape')::json) link_ids on true
                 join TSADV_BPM_ROLES_LINK rol_link on rol_link.id = link_ids::uuid and rol_link.delete_ts is null
        where v.name_ = 'rolesLinks'
    ),
         outcomes as (
             select j."outcomeId"                 as outcome_id,
                    j."executionId"               as execution_id,
                    j."taskDefinitionKey"         as task_definition_key,
                    j."user"                      as user_id,
                    to_timestamp(j."date" / 1000) as date,
                    j."date"                      as date_str
             from act_hi_varinst a
                      join act_ge_bytearray b on b.id_ = a.bytearray_id_
                      join json_populate_recordset(null::bproc_outcome,
                                                   ((encode(b.bytes_, 'escape')::json ->> 'outcomes')::json)) as j
                           on true
         ),
         bprocinst as (
             select ahp.start_user_id_::uuid as initiator_id,
                    ahp.start_time_,
                    task_comment.text_       as task_comment,
                    aht.name_                as task_name,
                    a.user_id                as approver_id,
                    dhr.id                   as role_id,
                    aht.start_time_,
                    aht.end_time_,
                    outcomes.outcome_id,
                    aht.end_time_,
                    ahp.business_key_
             from act_hi_procinst ahp
                      join act_hi_taskinst aht on aht.proc_inst_id_ = ahp.id_
                      left join links links
                                on links.proc_inst_id_ = ahp.id_ and links.bproc_user_task_code = aht.task_def_key_
                      left join tsadv_dic_hr_role dhr on links.hr_role_id = dhr.id
                      left join tsadv_bproc_actors a on aht.task_def_key_ = a.bproc_user_task_code and
                                                        a.entity_id::text ~ ahp.business_key_ and
                                                        (aht.assignee_ is null or aht.assignee_::uuid = a.user_id) and
                                                        a.delete_ts is null
                      left join sec_user su on a.user_id = su.id and su.delete_ts is null
                      left join outcomes outcomes
                                on outcomes.execution_id = aht.execution_id_ and outcomes.user_id = su.id and
                                   (outcomes.date - aht.end_time_ < '1 second'::interval and
                                    aht.end_time_ - outcomes.date < '1 second'::INTERVAL)
                      left join act_ru_variable task_comment on task_comment.proc_inst_id_ = ahp.id_
                 and task_comment.name_ = (outcomes.task_definition_key || outcomes.date_str || outcomes.user_id)
         )
    select ua.id                                     as id,
           ua."version",
           ua.create_ts,
           ua.created_by,
           ua.update_ts,
           ua.updated_by,
           ua.delete_ts,
           ua.deleted_by,
           ua.id                                     AS activity_id,
           coalesce(ar.request_number, pl.request_number, arr.request_number, vcr.request_number,
                    sor.request_number, afr.request_number, cr.request_number, cadr.request_number,
                    osr.request_number)              as order_code,
           uat.lang_value1                           as process_ru,
           coalesce(uat.lang_value3, uat.lang_value3, uat.lang_value3,
                    uat.lang_value3, uat.lang_value3, uat.lang_value3, uat.lang_value3, uat.lang_value3,
                    uat.lang_value3)                 as process_en,
           coalesce(ar.person_group_id, pl.assigned_person_id, arr.person_group_id,
                    vcr.person_group_id, sor.person_group_id, afr.employee_id,
                    cr.person_group_id,
                    cadr.employee_id, osr.author_id) as person_group_id,
           coalesce(ar.request_date, pl.request_date, arr.request_date,
                    vcr.request_date, sor.request_date, afr.request_date, cr.request_date,
                    cadr.request_date)::timestamp    as order_date,
           ua.status                                 as status,
           ua.create_ts                              as start_date,
           null                                      as expiry_date,
           false                                     as is_expired_task,
           null                                      as detail_ru,
           null                                      as detail_en
    from bprocinst bproc
             left join tsadv_assigned_performance_plan pl on bproc.business_key_ like (pl.id::text || '%')
             left join tsadv_absence_request ar on bproc.business_key_ = ar::text
             left join tsadv_absence_rvd_request arr on bproc.business_key_ = arr::text
             left join tsadv_vacation_schedule_request vcr on bproc.business_key_ = vcr::text
             left join tsadv_schedule_offsets_request sor on bproc.business_key_ = sor::text
             left join tsadv_absence_for_recall afr on bproc.business_key_ = afr::text
             left join tsadv_certificate_request cr on bproc.business_key_ = cr::text
             left join tsadv_change_absence_days_request cadr on bproc.business_key_ = cadr::text
             left join tsadv_org_structure_request osr on bproc.business_key_ = osr::text
             join uactivity_activity ua
on ua.reference_id = coalesce(pl.id, ar.id, arr.id, vcr.id, sor.id, afr.id, cr.id, cadr.id, osr.id)
join uactivity_activity_type uat on uat.id = ua.type_id;