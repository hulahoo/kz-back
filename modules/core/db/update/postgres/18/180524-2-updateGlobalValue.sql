alter table TSADV_GLOBAL_VALUE add column CODE varchar(255) ^
update TSADV_GLOBAL_VALUE set CODE = '' where CODE is null ;
alter table TSADV_GLOBAL_VALUE alter column CODE set not null ;
