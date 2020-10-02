update TSADV_DIC_ABSENCE_TYPE set USE_IN_BALANCE = false where USE_IN_BALANCE is null ;
alter table TSADV_DIC_ABSENCE_TYPE alter column USE_IN_BALANCE set not null ;
