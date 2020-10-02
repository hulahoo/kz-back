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