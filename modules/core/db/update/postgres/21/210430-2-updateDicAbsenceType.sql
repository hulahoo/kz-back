alter table TSADV_DIC_ABSENCE_TYPE add column IS_FILE_REQUIRED boolean ^
update TSADV_DIC_ABSENCE_TYPE set IS_FILE_REQUIRED = false where IS_FILE_REQUIRED is null ;
alter table TSADV_DIC_ABSENCE_TYPE alter column IS_FILE_REQUIRED set not null ;
