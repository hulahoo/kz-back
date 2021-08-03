create or replace view tsadv_bpm_proc_instance_vw
as
select a.id_::uuid             as id,
       a.start_user_id_::uuid  as start_user_id,
       a.start_time_           as start_time,
       a.end_time_             as end_time,
       a.end_time_ is not null as active,

       1                       as version,
       null                    as COMMENT_,
       null                    as ORGANIZATION_BIN,
       null                    as LEGACY_ID,
       null                    as INTEGRATION_USER_LOGIN,

       r.create_ts,
       r.created_by,
       r.update_ts,
       r.updated_by,
       r.delete_ts,
       r.deleted_by,
       r.status_id,
       r.request_date,
       r.request_number,
       r.id                    as entity_id,
       r.entity_name,
       r.process_ru,
       r.process_kz,
       r.process_en

from act_hi_procinst a
         join bproc_request_vw r on a.business_key_ like r.id::text || '%';