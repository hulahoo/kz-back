alter table BASE_PERSON rename column prev_job_obligation_id to prev_job_obligation_id__u66551 ;
alter table BASE_PERSON drop constraint FK_BASE_PERSON_PREV_JOB_OBLIGATION ;
drop index IDX_BASE_PERSON_PREV_JOB_OBLIGATION ;
alter table BASE_PERSON add column PREV_JOB_OBLIGATION varchar(50) ;
