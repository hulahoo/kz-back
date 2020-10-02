alter table TSADV_SALARY_REQUEST rename column old_start_date to old_start_date__u67440 ;
alter table TSADV_SALARY_REQUEST alter column old_start_date__u67440 drop not null ;
alter table TSADV_SALARY_REQUEST rename column old_amount to old_amount__u04595 ;
alter table TSADV_SALARY_REQUEST add column OLD_SALARY_ID uuid ;
