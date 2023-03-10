alter table BASE_PERSON add column COMMITMENTS_FROM_PREV_JOB varchar(50) ;
alter table BASE_PERSON add column REGISTERED_DISPENSARY varchar(50) ;
alter table BASE_PERSON add column CRIMINAL_ADMINISTRATIVE_LIABILITY varchar(50) ;
alter table BASE_PERSON add column COMMITMENTS_CREDIT boolean ;
alter table BASE_PERSON add column CHILD_UNDER18_WITHOUT_FATHER_OR_MOTHER varchar(50) ;
alter table BASE_PERSON add column DISABILITY varchar(50) ;
alter table BASE_PERSON add column CHILD_UNDER14_WITHOUT_FATHER_OR_MOTHER varchar(50) ;
alter table BASE_PERSON add column CONTRAINDICATIONS_HEALTH varchar(50) ;
alter table BASE_PERSON add column COMMITMENTS_LOAN boolean ;
alter table BASE_PERSON add column PREV_JOB_HR varchar(255) ;
alter table BASE_PERSON add column CONTRAINDICATIONS_HEALTH_TEXT varchar(255) ;
alter table BASE_PERSON add column DISPENSARY_PERIOD varchar(255) ;
alter table BASE_PERSON add column COMMITMENTS_NOT_SUR_MAT_VALUES boolean ;
alter table BASE_PERSON add column REASON_FOR_DISMISSAL varchar(255) ;
alter table BASE_PERSON add column HAVE_NDA varchar(50) ;
alter table BASE_PERSON add column HAVE_CONVICTION varchar(50) ;
alter table BASE_PERSON add column DISABILITY_GROUP varchar(255) ;
alter table BASE_PERSON add column CRIMINAL_ADMINISTRATIVE_LIABILITY_PERIOID_REASON varchar(255) ;
