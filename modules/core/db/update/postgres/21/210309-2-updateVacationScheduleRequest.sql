alter table TSADV_VACATION_SCHEDULE_REQUEST add column ATTACHMENT_ID uuid ;
alter table TSADV_VACATION_SCHEDULE_REQUEST add column SENT_TO_ORACLE boolean ^
update TSADV_VACATION_SCHEDULE_REQUEST set SENT_TO_ORACLE = false where SENT_TO_ORACLE is null ;
alter table TSADV_VACATION_SCHEDULE_REQUEST alter column SENT_TO_ORACLE set not null ;
