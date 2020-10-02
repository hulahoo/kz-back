update TSADV_JOB_REQUEST set SELECTED_BY_MANAGER = false where SELECTED_BY_MANAGER is null ;
alter table TSADV_JOB_REQUEST alter column SELECTED_BY_MANAGER set not null ;
