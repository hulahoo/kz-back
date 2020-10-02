alter table TSADV_TALENT_PROGRAM_PERSON_STEP rename column address to ADDRESS_RU ;
alter table TSADV_TALENT_PROGRAM_PERSON_STEP add column ADDRESS_EN varchar(255) ;
alter table TSADV_TALENT_PROGRAM_PERSON_STEP add column COMMENT_ varchar(255) ;
alter table TSADV_TALENT_PROGRAM_PERSON_STEP add column RESULT_ integer ;
alter table TSADV_TALENT_PROGRAM_PERSON_STEP add column FILE_ID uuid ;
