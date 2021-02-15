alter table TSADV_DIC_ABSENCE_TYPE add column INCLUDE_CALC_GZP boolean ^
update TSADV_DIC_ABSENCE_TYPE set INCLUDE_CALC_GZP = false where INCLUDE_CALC_GZP is null ;
alter table TSADV_DIC_ABSENCE_TYPE alter column INCLUDE_CALC_GZP set not null ;
