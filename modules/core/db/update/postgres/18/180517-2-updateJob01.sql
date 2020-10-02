alter table TSADV_JOB rename column job_name to job_name__u29961 ;
alter table TSADV_JOB alter column job_name__u29961 drop not null ;
alter table TSADV_JOB add column JOB_NAME_LANG1 varchar(1000) ^
--custom script
update TSADV_JOB set JOB_NAME_LANG1 = COALESCE (JOB_NAME__U29961, '') ;
alter table TSADV_JOB alter column JOB_NAME_LANG1 set not null ;
alter table TSADV_JOB add column JOB_NAME_LANG2 varchar(1000) ;
alter table TSADV_JOB add column JOB_NAME_LANG3 varchar(1000) ;
alter table TSADV_JOB add column JOB_NAME_LANG4 varchar(1000) ;
alter table TSADV_JOB add column JOB_NAME_LANG5 varchar(1000) ;
