alter table TSADV_HIRING_STEP add column IS_JOB_TEST boolean ^
update TSADV_HIRING_STEP set IS_JOB_TEST = false where IS_JOB_TEST is null ;
alter table TSADV_HIRING_STEP alter column IS_JOB_TEST set not null ;
