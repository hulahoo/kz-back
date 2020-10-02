alter table TSADV_SALARY rename column amount to amount__u65903 ;
alter table TSADV_SALARY alter column amount__u65903 drop not null ;
alter table TSADV_SALARY add column AMOUNT double precision ^
update TSADV_SALARY set AMOUNT = 0 where AMOUNT is null ;
alter table TSADV_SALARY alter column AMOUNT set not null ;
