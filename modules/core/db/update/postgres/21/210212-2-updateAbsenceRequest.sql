alter table TSADV_ABSENCE_REQUEST rename column new_end_date to new_end_date__u07870 ;
alter table TSADV_ABSENCE_REQUEST rename column reason to reason__u68085 ;
alter table TSADV_ABSENCE_REQUEST add column VACATION_SCHEDULE_ID uuid ;
alter table TSADV_ABSENCE_REQUEST add column NEW_END_DATE date ;
