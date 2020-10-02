alter table TSADV_SALARY_REQUEST rename column old_salary_id to old_salary_id__u41300 ;
drop index IDX_TSADV_SALARY_REQUEST_OLD_SALARY ;
alter table TSADV_SALARY_REQUEST drop constraint FK_TSADV_SALARY_REQUEST_OLD_SALARY ;
alter table TSADV_SALARY_REQUEST add column REQUEST_NUMBER varchar(255) ;
alter table TSADV_SALARY_REQUEST add column OLD_SALARY uuid ;
