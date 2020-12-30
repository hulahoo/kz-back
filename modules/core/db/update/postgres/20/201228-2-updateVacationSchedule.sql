alter table TSADV_VACATION_SCHEDULE rename column status_id to status_id__u79027 ;
alter table TSADV_VACATION_SCHEDULE alter column status_id__u79027 drop not null ;
drop index IDX_TSADV_VACATION_SCHEDULE_STATUS ;
-- alter table TSADV_VACATION_SCHEDULE add column STATUS_ID uuid ^
-- update TSADV_VACATION_SCHEDULE set STATUS_ID = <default_value> ;
-- alter table TSADV_VACATION_SCHEDULE alter column status_id set not null ;
alter table TSADV_VACATION_SCHEDULE add column STATUS_ID uuid not null ;
