drop view if exists TSADV_ALL_ABSENCE_REQUEST;

CREATE VIEW TSADV_ALL_ABSENCE_REQUEST as
  SELECT r.id, r."version", r.create_ts, r.created_by, r.update_ts, r.updated_by, r.delete_ts, r.deleted_by,
  r.date_from as start_date, r.date_to as end_date, r.person_group_id, r.absence_days, r.status_id, r.type_id, r.request_date,  r.request_number,
  'tsadv$AbsenceRequest' as entity_name, r.comment_ , r.integration_user_login , r.legacy_id, r.organization_bin
  from tsadv_absence_request r where r.delete_ts is null
  union
  SELECT l.id, l."version", l.create_ts, l.created_by, l.update_ts, l.updated_by, l.delete_ts, l.deleted_by,
  l.start_date, l.end_date, ab.person_group_id , null, l.status_id, sys_config_value('tal.hr.absenceType.leavingVacationRequest')::uuid as type_id, l.request_date, l.request_number ,
  'tsadv$LeavingVacationRequest' as entity_name, l.comment_ , l.integration_user_login , l.legacy_id , l.organization_bin
  from tsadv_leaving_vacation_request l
  join tsadv_absence ab on ab.id = l.vacation_id
  where l.delete_ts is null
  union
  SELECT l.id, l."version", l.create_ts, l.created_by, l.update_ts, l.updated_by, l.delete_ts, l.deleted_by,
  l.date_from as start_date, l.date_to as end_date, ab.person_group_id , null, l.status_id, sys_config_value('tal.hr.absenceType.absenceForRecall')::uuid as type_id, l.request_date, l.request_number ,
  'tsadv_AbsenceForRecall' as entity_name, l.comment_ , l.integration_user_login , l.legacy_id , l.organization_bin
  from TSADV_ABSENCE_FOR_RECALL l
  join tsadv_absence ab on ab.id = l.vacation_id
  where l.delete_ts is null
  union
  SELECT l.id, l."version", l.create_ts, l.created_by, l.update_ts, l.updated_by, l.delete_ts, l.deleted_by,
  l.NEW_START_DATE as start_date, l.NEW_END_DATE as end_date, ab.person_group_id , null, l.status_id, sys_config_value('tal.hr.absenceType.changeAbsenceDaysRequest')::uuid as type_id, l.request_date, l.request_number ,
  'tsadv_ChangeAbsenceDaysRequest' as entity_name, l.comment_ , l.integration_user_login , l.legacy_id , l.organization_bin
  from TSADV_CHANGE_ABSENCE_DAYS_REQUEST l
  join tsadv_absence ab on ab.id = l.vacation_id
  where l.delete_ts is null;
