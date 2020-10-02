-- update TSADV_CASE_TYPE set CODE = <default_value> where CODE is null ;
alter table TSADV_CASE_TYPE alter column CODE set not null ;
