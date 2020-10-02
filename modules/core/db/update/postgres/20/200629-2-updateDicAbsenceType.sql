update TSADV_DIC_ABSENCE_TYPE set IGNORE_HOLIDAYS = false where IGNORE_HOLIDAYS is null ;
alter table TSADV_DIC_ABSENCE_TYPE alter column IGNORE_HOLIDAYS set not null ;
