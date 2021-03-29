alter table TSADV_DIC_ABSENCE_TYPE add column IS_VACATION_DATE boolean ^
update TSADV_DIC_ABSENCE_TYPE set IS_VACATION_DATE = false where IS_VACATION_DATE is null ;
alter table TSADV_DIC_ABSENCE_TYPE alter column IS_VACATION_DATE set not null ;
