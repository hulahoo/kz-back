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
