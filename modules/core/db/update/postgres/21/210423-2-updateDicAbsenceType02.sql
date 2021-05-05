update TSADV_DIC_ABSENCE_TYPE set ECOLOGICAL_ABSENCE = false where ECOLOGICAL_ABSENCE is null ;
alter table TSADV_DIC_ABSENCE_TYPE alter column ECOLOGICAL_ABSENCE set not null ;
