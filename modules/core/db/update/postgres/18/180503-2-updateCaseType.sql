-- alter table TSADV_CASE_TYPE add column CODE varchar(255) ^
-- update TSADV_CASE_TYPE set CODE = <default_value> ;
-- alter table TSADV_CASE_TYPE alter column CODE set not null ;
alter table TSADV_CASE_TYPE add column CODE varchar(255) ;
