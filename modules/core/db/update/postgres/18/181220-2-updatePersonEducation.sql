alter table TSADV_PERSON_EDUCATION add column DIPLOMA_NUMBER varchar(255) ;
alter table TSADV_PERSON_EDUCATION add column GRADUATION_DATE date ;
alter table TSADV_PERSON_EDUCATION add column FOREIGN_EDUCATION boolean ^
update TSADV_PERSON_EDUCATION set FOREIGN_EDUCATION = false where FOREIGN_EDUCATION is null ;
alter table TSADV_PERSON_EDUCATION alter column FOREIGN_EDUCATION set not null ;
