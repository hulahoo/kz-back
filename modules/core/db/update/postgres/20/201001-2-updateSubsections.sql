alter table TSADV_SUBSECTIONS add column INTEGRATION_USER_LOGIN varchar(255) ;
alter table TSADV_SUBSECTIONS add column IS_DEFAULT boolean ^
update TSADV_SUBSECTIONS set IS_DEFAULT = false where IS_DEFAULT is null ;
alter table TSADV_SUBSECTIONS alter column IS_DEFAULT set not null ;
alter table TSADV_SUBSECTIONS add column ORGANIZATION_BIN varchar(255) ;
