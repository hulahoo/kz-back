alter table TSADV_GRADE add column RECOGNITION_NOMINATE boolean ^
update TSADV_GRADE set RECOGNITION_NOMINATE = false where RECOGNITION_NOMINATE is null ;
alter table TSADV_GRADE alter column RECOGNITION_NOMINATE set not null ;
