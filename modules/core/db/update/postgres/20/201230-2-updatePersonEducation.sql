alter table TSADV_PERSON_EDUCATION add column FACULTY varchar(2000) ;
alter table TSADV_PERSON_EDUCATION add column EDUCATION_TYPE_ID uuid ;
alter table TSADV_PERSON_EDUCATION add column EDUCATIONAL_ESTABLISHMENT_ID uuid ;
alter table TSADV_PERSON_EDUCATION add column START_DATE_HISTORY date ;
alter table TSADV_PERSON_EDUCATION add column QUALIFICATION varchar(2000) ;
alter table TSADV_PERSON_EDUCATION add column END_DATE_HISTORY date ;
alter table TSADV_PERSON_EDUCATION add column FORM_STUDY_ID uuid ;
