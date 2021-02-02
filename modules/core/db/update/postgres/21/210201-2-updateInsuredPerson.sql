alter table TSADV_INSURED_PERSON rename column file_id to file_id__u47461 ;
alter table TSADV_INSURED_PERSON drop constraint FK_TSADV_INSURED_PERSON_FILE ;
drop index IDX_TSADV_INSURED_PERSON_FILE ;
alter table TSADV_INSURED_PERSON add column JOB_MEMBER varchar(255) ;
