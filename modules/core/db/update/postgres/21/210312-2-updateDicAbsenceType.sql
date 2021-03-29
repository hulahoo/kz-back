alter table TSADV_DIC_ABSENCE_TYPE add column IS_ORIGINAL_SHEET boolean ^
update TSADV_DIC_ABSENCE_TYPE set IS_ORIGINAL_SHEET = false where IS_ORIGINAL_SHEET is null ;
alter table TSADV_DIC_ABSENCE_TYPE alter column IS_ORIGINAL_SHEET set not null ;
alter table TSADV_DIC_ABSENCE_TYPE add column IS_JUST_REQUIRED boolean ^
update TSADV_DIC_ABSENCE_TYPE set IS_JUST_REQUIRED = false where IS_JUST_REQUIRED is null ;
alter table TSADV_DIC_ABSENCE_TYPE alter column IS_JUST_REQUIRED set not null ;
alter table TSADV_DIC_ABSENCE_TYPE add column IS_CHECK_WORK boolean ^
update TSADV_DIC_ABSENCE_TYPE set IS_CHECK_WORK = false where IS_CHECK_WORK is null ;
alter table TSADV_DIC_ABSENCE_TYPE alter column IS_CHECK_WORK set not null ;
