alter table TSADV_ABSENCE_REQUEST rename column vacation_schedule_id to vacation_schedule_id__u83172 ;
drop index IDX_TSADV_ABSENCE_REQUEST_VACATION_SCHEDULE ;
alter table TSADV_ABSENCE_REQUEST add column VACATION_SCHEDULE_REQUEST_ID uuid ;