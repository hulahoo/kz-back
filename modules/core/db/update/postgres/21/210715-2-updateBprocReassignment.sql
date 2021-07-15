delete from TSADV_BPROC_REASSIGNMENT;
alter table TSADV_BPROC_REASSIGNMENT add column TASK_DEFINITION_KEY varchar(255) ^
alter table TSADV_BPROC_REASSIGNMENT alter column TASK_DEFINITION_KEY set not null ;
update TSADV_BPROC_REASSIGNMENT set END_TIME = current_timestamp where END_TIME is null ;
alter table TSADV_BPROC_REASSIGNMENT alter column END_TIME set not null ;
