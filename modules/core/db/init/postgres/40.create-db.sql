
-- Вся История обучения: как в системе (enrollment) так и вне системы (person_learning_history)
-- the last version

drop view if exists tsadv_person_all_learning_history_v;

create or replace view tsadv_person_all_learning_history_v as
    select person_group_id,
           course_id,
           course_name,
           start_date,
           end_date,
           'OUT_THE_SYSTEM' as type_,
           id,
           version,
           create_ts,
           created_by,
           update_ts,
           updated_by,
           delete_ts,
           deleted_by
    from tsadv_person_learning_history h
    union
    select en.person_group_id,
           en.course_id,
           en.course_name,
           least(en.attempt_start_date, en.sessions_start_date)::date as start_date,
           greatest(en.attempt_end_date, en.sessions_end_date)::date as end_date,
           en.type_,
           en.id,
           en.version,
           en.create_ts,
           en.created_by,
           en.update_ts,
           en.updated_by,
           en.delete_ts,
           en.deleted_by
    from (
             select e.person_group_id,
                    e.course_id,
                    c.name as course_name,
                    (select min(a.attempt_date)
                     from tsadv_course_section_attempt a
                     where a.enrollment_id = e.id
                       and a.success = true
                    ) as attempt_start_date,
                    (select max(a.attempt_date)
                     from tsadv_course_section_attempt a
                     where a.enrollment_id = e.id
                       and a.success = true
                    ) as attempt_end_date,
                    (select min(s.start_date)
                     from tsadv_course_section_session s
                              join tsadv_course_section sc on  s.course_section_id = sc.id
                     where sc.course_id = e.course_id
                       and s.delete_ts is null
                       and sc.delete_ts is null
                    ) as sessions_start_date,
                    (select max(s.start_date)
                     from tsadv_course_section_session s
                              join tsadv_course_section sc on  s.course_section_id = sc.id
                     where sc.course_id = e.course_id
                       and s.delete_ts is null
                       and sc.delete_ts is null
                    ) as sessions_end_date,
                    'IN_THE_SYSTEM' as type_,
                    e.id,
                    e.version,
                    e.create_ts,
                    e.created_by,
                    e.update_ts,
                    e.updated_by,
                    e.delete_ts,
                    e.deleted_by
             from tsadv_enrollment e
                      join tsadv_course c on e.course_id = c.id
             where e.status = 5 -- COMPLETED
         ) en
;

-- Все работники по аттестациям исходя из настроек      https://apps.uco.kz/confluence/pages/viewpage.action?pageId=14254093
create or replace view tsadv_all_persons_for_attestations_v as
with param as (
         select a.id,
                (select count(*) as count
                   from tsadv_attestation_organization ao
                  where a.id = ao.attestation_id
                    and ao.delete_ts is null) as count_org,
                (select count(*) AS count
                   from tsadv_attestation_position ap
                  where a.id = ap.attestation_id
                    and ap.delete_ts is null) as count_pos,
                (select count(*) AS count
                   from tsadv_attestation_job aj
                  where a.id = aj.attestation_id
                    and aj.delete_ts is null) as count_job,
                a.start_date
           from tsadv_attestation a
     ),
     org as (
         select ao.attestation_id,
                o.group_id,
                o.id,
                org_str.organization_group_id
           from base_organization o
           join tsadv_organization_structure org_str on org_str.path::text ~~ (('%'::text || o.group_id) || '%'::text)
                                                     or org_str.organization_group_id = o.group_id
           join tsadv_attestation_organization ao on  ao.organization_group_id = o.group_id
                                                  and ao.delete_ts is null
           join tsadv_attestation att on att.id = ao.attestation_id
          where true
            and att.start_date >= org_str.start_date
            and att.start_date <= org_str.end_date
            and att.start_date >= o.start_date
            and att.start_date <= o.end_date
            and o.delete_ts is null
            and 1 = case
                       when ao.include_child = true then 1
                       when ao.include_child = false
                        and o.group_id = org_str.organization_group_id then 1
                       else 0
                    end
     )
select pp.group_id as person_group_id,
       param.id as attestation_id
  from base_assignment aa
  join param on  param.start_date >= aa.start_date
             and param.start_date <= aa.end_date
  join base_person pp on  pp.group_id = aa.person_group_id
                      and param.start_date >= pp.start_date
                      and param.start_date <= pp.end_date
  join tsadv_dic_assignment_status ast on aa.assignment_status_id = ast.id
 where true
   and aa.delete_ts is null
   and aa.primary_flag = true
   and ast.code::text = 'ACTIVE'::text
   and 1 = case
              when param.count_org = 0 then 1
              when param.count_org > 0
               and exists(
                      select org.organization_group_id
                        from org
                       where org.attestation_id = param.id
                         and aa.organization_group_id = org.organization_group_id
                   ) then 1
              else 0
           end
   and 1 = case
              when param.count_pos = 0 then 1
              when param.count_pos > 0
               and exists(
                      select pos.position_group_id
                        from tsadv_attestation_position pos
                       where pos.delete_ts is null
                         and pos.attestation_id = param.id
                         and aa.position_group_id = pos.position_group_id
                   ) then 1
              else 0
           end
   and 1 = case
              when param.count_job = 0 then 1
              when param.count_job > 0
               and exists(
                      select job.job_group_id
                        from tsadv_attestation_job job
                       where job.delete_ts is null
                         and job.attestation_id = param.id
                         and aa.job_group_id = job.job_group_id
                   ) then 1
              else 0
           end
;


-- Все работники по аттестаци (таблица для производительности)      https://apps.uco.kz/confluence/pages/viewpage.action?pageId=14254093
create table if not exists xxtsadv_all_persons_for_attestation_t (
    person_group_id uuid not null,
    attestation_id uuid not null
);


-- Все новые-невключённые работники по аттестаци      https://apps.uco.kz/confluence/pages/viewpage.action?pageId=14254093
create or replace view tsadv_person_group_attestation_v as
select t.person_group_id,
       attestation_id
  from xxtsadv_all_persons_for_attestation_t t
 where not exists(
              select atp.person_group_id
                from tsadv_attestation_participant atp
               where atp.attestation_id = t.attestation_id
                 and atp.person_group_id = t.person_group_id
                 and atp.delete_ts is null
           )
;


create or replace function refresh_organization_structure() -- Обновление Организационной структуры (по ночам)
    returns character varying
    language plpgsql
as
$function$
begin
    delete from tsadv_organization_structure;
    insert into tsadv_organization_structure (
        id,
        hierarchy_id,
        parent_id,
        element_type,
        organization_group_id,
        parent_organization_group_id,
        start_date,
        end_date,
        path,
        _level,
        path_org_name1,
        path_org_name2,
        path_org_name3,
        version,
        create_ts,
        created_by,
        update_ts,
        updated_by,
        delete_ts,
        deleted_by
    )
    WITH RECURSIVE org_pos(
                           id,
                           hierarchy_id,
                           parent_id,
                           element_type,
                           organization_group_id,
                           parent_organization_group_id,
                           start_date,
                           end_date,
                           path,
                           level,
                           path_org_name1,
                           path_org_name2,
                           path_org_name3
        ) AS (
        SELECT he.id,
               he.hierarchy_id,
               he.parent_id,
               he.element_type,
               he.organization_group_id,
               NULL::uuid                                                       AS parent_organization_group_id,
               he.start_date,
               he.end_date,
               he.organization_group_id::character varying                      AS path,
               1,
               COALESCE(o.organization_name_lang1, ''::character varying)::text AS path_org_name1,
               COALESCE(o.organization_name_lang2, ''::character varying)::text AS path_org_name2,
               COALESCE(o.organization_name_lang3, ''::character varying)::text AS path_org_name3
        FROM base_hierarchy_element he
                 JOIN base_hierarchy h ON h.id = he.hierarchy_id
                 JOIN base_organization o ON he.organization_group_id = o.group_id
            and current_date between o.start_date and o.end_date
            and o.delete_ts is null
        WHERE he.parent_id IS NULL
        UNION ALL
        SELECT he.id,
               he.hierarchy_id,
               he.parent_id,
               he.element_type,
               COALESCE(he.organization_group_id, op.organization_group_id)                                  AS organization_group_id,
               COALESCE(op.organization_group_id, op.parent_organization_group_id)                           AS parent_organization_group_id,
               he.start_date,
               he.end_date,
               ((op.path::text || '*'::text) || he.organization_group_id)::character varying                 AS "varchar",
               op.level + 1,
               op.path_org_name1 || '->' ||
               COALESCE(o.organization_name_lang1, ''::character varying)::text                              AS path_org_name1,
               op.path_org_name2 || '->' ||
               COALESCE(o.organization_name_lang2, ''::character varying)::text                              AS path_org_name2,
               op.path_org_name3 || '->' ||
               COALESCE(o.organization_name_lang3, ''::character varying)::text                              AS path_org_name3
        FROM base_hierarchy_element he
                 JOIN org_pos op ON he.parent_id = op.id
                 JOIN base_organization o ON he.organization_group_id = o.group_id
            and current_date between o.start_date and o.end_date
            and o.delete_ts is null
        WHERE he.element_type = 1
          and he.delete_ts is null
    )
    SELECT org_pos.id,
           org_pos.hierarchy_id,
           org_pos.parent_id,
           org_pos.element_type,
           org_pos.organization_group_id,
           org_pos.parent_organization_group_id,
           org_pos.start_date,
           org_pos.end_date,
           org_pos.path,
           org_pos.level,
           org_pos.path_org_name1,
           org_pos.path_org_name2,
           org_pos.path_org_name3,
           1::integer                        AS version,
           NULL::timestamp without time zone AS create_ts,
           NULL::character varying           AS created_by,
           current_timestamp                 AS update_ts,
           NULL::character varying           AS updated_by,
           NULL::timestamp without time zone AS delete_ts,
           NULL::character varying           AS deleted_by
    FROM org_pos;

    return 'success';
end;
$function$
;

