alter table TSADV_GRADE_GROUP add column AVAILABLE_SALARY boolean ^
update TSADV_GRADE_GROUP set AVAILABLE_SALARY = false where AVAILABLE_SALARY is null ;
alter table TSADV_GRADE_GROUP alter column AVAILABLE_SALARY set not null ;
