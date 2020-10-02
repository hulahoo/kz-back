update TSADV_GLOBAL_VALUE set CODE = 'ANNUAL_DAYS_COUNT' where CODE is null ;
alter table TSADV_GLOBAL_VALUE alter column CODE set not null ;
