alter table TSADV_JOB_REQUEST add column SENT boolean ^
update TSADV_JOB_REQUEST set SENT = false where SENT is null ;
alter table TSADV_JOB_REQUEST alter column SENT set not null ;
