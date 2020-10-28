alter table TSADV_JOB_GROUP add column JOB_NAME_LANG2 varchar(1000) ;
alter table TSADV_JOB_GROUP add column JOB_NAME_LANG5 varchar(1000) ;
alter table TSADV_JOB_GROUP add column JOB_NAME_LANG1 varchar(1000) ^
update TSADV_JOB_GROUP set JOB_NAME_LANG1 = '' where JOB_NAME_LANG1 is null ;
alter table TSADV_JOB_GROUP alter column JOB_NAME_LANG1 set not null ;
alter table TSADV_JOB_GROUP add column JOB_CATEGORY_ID uuid ;
alter table TSADV_JOB_GROUP add column JOB_NAME_LANG3 varchar(1000) ;
alter table TSADV_JOB_GROUP add column EMPLOYEE_CATEGORY_ID uuid ;
alter table TSADV_JOB_GROUP add column JOB_NAME_LANG4 varchar(1000) ;
