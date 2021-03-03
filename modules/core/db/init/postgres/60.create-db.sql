
--views
drop view if exists TSADV_ALL_ABSENCE_REQUEST;
CREATE VIEW TSADV_ALL_ABSENCE_REQUEST AS
  SELECT r.id, r."version", r.create_ts, r.created_by, r.update_ts, r.updated_by, r.delete_ts, r.deleted_by,
  r.date_from as start_date, r.date_to as end_date, r.person_group_id, r.absence_days, r.status_id, r.type_id, r.request_date,  r.request_number,
  'tsadv$AbsenceRequest' as entity_name, r.comment_ , r.integration_user_login , r.legacy_id, r.organization_bin
  from tsadv_absence_request r where r.delete_ts is null
  union
  SELECT l.id, l."version", l.create_ts, l.created_by, l.update_ts, l.updated_by, l.delete_ts, l.deleted_by,
  l.start_date, l.end_date, ab.person_group_id , null, l.status_id, a.id as type_id, l.request_date, l.request_number ,
  'tsadv$LeavingVacationRequest' as entity_name, l.comment_ , l.integration_user_login , l.legacy_id , l.organization_bin
  from tsadv_leaving_vacation_request l
  join tsadv_absence ab on ab.id = l.vacation_id
  left join tsadv_dic_absence_type a on a.code = 'MATERNITY' and a.delete_ts is null
  where l.delete_ts is null;
  union
  SELECT l.id, l."version", l.create_ts, l.created_by, l.update_ts, l.updated_by, l.delete_ts, l.deleted_by,
  l.start_date, l.end_date, l.person_group_id , l.ABSENCE_DAYS , l.status_id, a.id as type_id, l.request_date, l.request_number ,
  'tsadv$LeavingVacationRequest' as entity_name, l.comment_ , l.integration_user_login , l.legacy_id , l.organization_bin
  from TSADV_VACATION_SCHEDULE_REQUEST l
  left join tsadv_dic_absence_type a on a.code = 'VACATION_SCHEDULE' and a.delete_ts is null
  where l.delete_ts is null;