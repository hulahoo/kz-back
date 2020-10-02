alter table TSADV_SALARY_REQUEST rename column amount to amount__u69208 ;
alter table TSADV_SALARY_REQUEST alter column amount__u69208 drop not null ;
alter table TSADV_SALARY_REQUEST add column AMOUNT double precision ^
update TSADV_SALARY_REQUEST set AMOUNT = 0 where AMOUNT is null ;
alter table TSADV_SALARY_REQUEST alter column AMOUNT set not null ;
