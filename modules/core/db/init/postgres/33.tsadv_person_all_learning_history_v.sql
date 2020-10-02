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
