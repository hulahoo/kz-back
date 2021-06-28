drop function gantt_chart_vacation_schedule(uuid, date, date, varchar);

create or replace function gantt_chart_vacation_schedule(p_person_group_id uuid,
                                                         p_start_date date,
                                                         p_end_date date,
                                                         p_lang_index character varying) returns json
    language plpgsql
as
$$
DECLARE
    l_json_data               json;
    arr_organization_group_id uuid[];
    arr_position_group_id     uuid[];
BEGIN

    with recursive
        hr_orgs as (
            select hr_user.organization_group_id
            from sec_user u
                     join TSADV_ORGANIZATION_HR_USER hr_user
                          on u.id = hr_user.user_id
                              and hr_user.delete_ts is null
                              and current_date between hr_user.date_from and hr_user.date_to
                     join tsadv_dic_hr_role r
                          on hr_user.hr_role_id = r.id
                              and r.delete_ts is null
                              and r.code = 'HR'
            where u.active is true
              and u.person_group_id = p_person_group_id
              and u.delete_ts is null
        ),
        orgs_with_child as (
            SELECT e.id,
                   e.organization_group_id,
                   e.group_id,
                   '0'::int as lvl
            from hr_orgs o
                     join base_hierarchy_element e
                          on e.organization_group_id = o.organization_group_id
                              and current_date between e.start_date and e.end_date
                              and e.delete_ts is null
                     join base_hierarchy h
                          on h.id = e.hierarchy_id
                              and h.delete_ts is null
                              and h.primary_flag is true
                     join base_hierarchy_element child
                          on child.parent_group_id = e.group_id
                              and child.delete_ts is null
                              and current_date between child.start_date and child.end_date
                              and child.hierarchy_id = h.id
            UNION
            SELECT e.id,
                   e.organization_group_id,
                   e.group_id,
                   p.lvl + '1' as lvl
            from base_hierarchy_element e
                     join base_hierarchy h
                          on h.id = e.hierarchy_id
                              and h.delete_ts is null
                              and h.primary_flag is true
                     join orgs_with_child as p
                          on e.parent_group_id = p.group_id
            where current_date between e.start_date and e.end_date
              and e.delete_ts is null
        )
    select array_agg(organization_group_id)
    into arr_organization_group_id
    from orgs_with_child;

    select array_agg(el_child.position_group_id)
    into arr_position_group_id
    from base_assignment s
             join tsadv_dic_assignment_status ss
                  on s.assignment_status_id = ss.id
                      and ss.delete_ts is null
                      and ss.code <> 'TERMINATED'
             join base_hierarchy_element el
                  on el.position_group_id = s.position_group_id
                      and el.delete_ts is null
                      and current_date between el.start_date and el.end_date
             join base_hierarchy_element el_child
                  on el_child.parent_group_id = el.group_id
                      and el_child.delete_ts is null
                      and current_date between el_child.start_date and el_child.end_date
    where s.person_group_id = p_person_group_id
      and s.delete_ts is null
      and current_date between s.start_date and s.end_date
      and s.primary_flag is true;

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
                          and (s.organization_group_id in (select unnest(arr_organization_group_id))
                              or s.position_group_id in (select unnest(arr_position_group_id)))
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
                          and (s.organization_group_id in (select unnest(arr_organization_group_id))
                              or s.position_group_id in (select unnest(arr_position_group_id)))
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

alter function gantt_chart_vacation_schedule(uuid, date, date, varchar) owner to tal;