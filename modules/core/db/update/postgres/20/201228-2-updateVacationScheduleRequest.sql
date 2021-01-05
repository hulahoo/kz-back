alter table TSADV_VACATION_SCHEDULE_REQUEST rename column status_id to status_id__u57365 ;
alter table TSADV_VACATION_SCHEDULE_REQUEST alter column status_id__u57365 drop not null ;
drop index if exists IDX_TSADV_VACATION_SCHEDULE_REQUEST_STATUS ;
alter table TSADV_VACATION_SCHEDULE_REQUEST add column STATUS_ID uuid not null ;
alter table TSADV_VACATION_SCHEDULE_REQUEST alter column REQUEST_NUMBER set data type bigint ;
