create or replace function gantt_chart_vacation_schedule(p_organization_group_id uuid,
                                                         p_start_date date,
                                                         p_end_date date,
                                                         p_lang_index varchar) returns json
    language plpgsql
as
$$
DECLARE
    l_json_data json;
BEGIN
    with ganttData as (
        select r.person_group_id                                                                    as person_group_id,
               concat(p.last_name || ' ', p.first_name || ' ', p.middle_name)                       as full_name,
               r.start_date                                                                         as start_date,
               r.end_date                                                                           as end_date,
               case when p_lang_index ~ 'en|EN' then 'Vacation schedule' else 'График отпусков' end as absence_type
        from tsadv_vacation_schedule_request r
                 join base_assignment s
                      on s.person_group_id = r.person_group_id
                          and s.delete_ts is null
                          and current_date between s.start_date and s.end_date
                          and s.primary_flag is true
                          and s.organization_group_id = p_organization_group_id
                 join tsadv_dic_assignment_status ss
                      on ss.id = s.assignment_status_id
                          and ss.delete_ts is null
                          and ss.code <> 'TERMINATED'
                 join base_person p
                      on p.group_id = r.person_group_id
                          and p.delete_ts is null
                          and current_date between p.start_date and p.end_date
        where r.delete_ts is null
          and p_start_date <= r.end_date
          and p_end_date >= r.start_date
        union
        select a.person_group_id                                                          as person_group_id,
               concat(p.last_name || ' ', p.first_name || ' ', p.middle_name)             as full_name,
               a.date_from                                                                as start_date,
               a.date_to                                                                  as end_date,
               case when p_lang_index ~ 'en|EN' then t.lang_value3 else t.lang_value1 end as absence_type
        from tsadv_absence a
                 join tsadv_dic_absence_type t
                      on t.id = a.type_id
                          and t.delete_ts is null
                 join base_assignment s
                      on s.person_group_id = a.person_group_id
                          and s.delete_ts is null
                          and current_date between s.start_date and s.end_date
                          and s.primary_flag is true
                          and s.organization_group_id = p_organization_group_id
                 join tsadv_dic_assignment_status ss
                      on ss.id = s.assignment_status_id
                          and ss.delete_ts is null
                          and ss.code <> 'TERMINATED'
                 join base_person p
                      on p.group_id = a.person_group_id
                          and p.delete_ts is null
                          and current_date between p.start_date and p.end_date
        where a.delete_ts is null
          and p_start_date <= a.date_to
          and p_end_date >= a.date_from
    )
    select json_agg(json_build_object('personFullName', r.full_name, 'startDate', r.start_date, 'endDate', r.end_date,
                                      'personGroupId', r.person_group_id, 'absenceType', r.absence_type))
    into l_json_data
    from ganttData r;
    RETURN l_json_data;
END ;
$$;

alter function gantt_chart_vacation_schedule(uuid,date,date,varchar) owner to tal;