alter table TSADV_STATUS_MSDS add column INTEGRATION_USER_LOGIN varchar(255) ;
alter table TSADV_STATUS_MSDS add column IS_DEFAULT boolean ^
update TSADV_STATUS_MSDS set IS_DEFAULT = false where IS_DEFAULT is null ;
alter table TSADV_STATUS_MSDS alter column IS_DEFAULT set not null ;
alter table TSADV_STATUS_MSDS add column ORGANIZATION_BIN varchar(255) ;
