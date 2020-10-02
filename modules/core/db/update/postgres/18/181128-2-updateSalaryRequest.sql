alter table TSADV_SALARY_REQUEST add column OLD_START_DATE date ^
update TSADV_SALARY_REQUEST set OLD_START_DATE = current_date where OLD_START_DATE is null ;
alter table TSADV_SALARY_REQUEST alter column OLD_START_DATE set not null ;
