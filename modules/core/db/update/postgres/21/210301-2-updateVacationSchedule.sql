alter table TSADV_VACATION_SCHEDULE rename column status_id to status_id__u66376 ;
alter table TSADV_VACATION_SCHEDULE alter column status_id__u66376 drop not null ;
alter table TSADV_VACATION_SCHEDULE drop constraint FK_TSADV_VACATION_SCHEDULE_STATUS ;
drop index IDX_TSADV_VACATION_SCHEDULE_STATUS ;
