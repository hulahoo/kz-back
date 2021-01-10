alter table TSADV_DIC_RECEIVING_TYPE add column ACTIVE boolean ^
update TSADV_DIC_RECEIVING_TYPE set ACTIVE = false where ACTIVE is null ;
alter table TSADV_DIC_RECEIVING_TYPE alter column ACTIVE set not null ;
alter table TSADV_DIC_RECEIVING_TYPE add column LANG_VALUE4 varchar(255) ;
alter table TSADV_DIC_RECEIVING_TYPE add column IS_SYSTEM_RECORD boolean ^
update TSADV_DIC_RECEIVING_TYPE set IS_SYSTEM_RECORD = false where IS_SYSTEM_RECORD is null ;
alter table TSADV_DIC_RECEIVING_TYPE alter column IS_SYSTEM_RECORD set not null ;
alter table TSADV_DIC_RECEIVING_TYPE add column DESCRIPTION3 varchar(2000) ;
alter table TSADV_DIC_RECEIVING_TYPE add column LEGACY_ID varchar(255) ;
alter table TSADV_DIC_RECEIVING_TYPE add column LANG_VALUE3 varchar(255) ;
alter table TSADV_DIC_RECEIVING_TYPE add column END_DATE date ;
alter table TSADV_DIC_RECEIVING_TYPE add column LANG_VALUE2 varchar(255) ;
alter table TSADV_DIC_RECEIVING_TYPE add column DESCRIPTION2 varchar(2000) ;
alter table TSADV_DIC_RECEIVING_TYPE add column ORDER_ integer ;
alter table TSADV_DIC_RECEIVING_TYPE add column CODE varchar(255) ;
alter table TSADV_DIC_RECEIVING_TYPE add column LANG_VALUE5 varchar(255) ;
-- alter table TSADV_DIC_RECEIVING_TYPE add column LANG_VALUE1 varchar(255) ^
-- update TSADV_DIC_RECEIVING_TYPE set LANG_VALUE1 = <default_value> ;
-- alter table TSADV_DIC_RECEIVING_TYPE alter column LANG_VALUE1 set not null ;
alter table TSADV_DIC_RECEIVING_TYPE add column LANG_VALUE1 varchar(255) ;
alter table TSADV_DIC_RECEIVING_TYPE add column DESCRIPTION1 varchar(2000) ;
alter table TSADV_DIC_RECEIVING_TYPE add column INTEGRATION_USER_LOGIN varchar(255) ;
alter table TSADV_DIC_RECEIVING_TYPE add column DESCRIPTION4 varchar(2000) ;
alter table TSADV_DIC_RECEIVING_TYPE add column IS_DEFAULT boolean ^
update TSADV_DIC_RECEIVING_TYPE set IS_DEFAULT = false where IS_DEFAULT is null ;
alter table TSADV_DIC_RECEIVING_TYPE alter column IS_DEFAULT set not null ;
alter table TSADV_DIC_RECEIVING_TYPE add column START_DATE date ;
alter table TSADV_DIC_RECEIVING_TYPE add column DESCRIPTION5 varchar(2000) ;
alter table TSADV_DIC_RECEIVING_TYPE add column ORGANIZATION_BIN varchar(255) ;
