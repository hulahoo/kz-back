update TSADV_DIC_ABSENCE_TYPE set IS_WORKING_DAY = false where IS_WORKING_DAY is null ;
alter table TSADV_DIC_ABSENCE_TYPE alter column IS_WORKING_DAY set not null ;
