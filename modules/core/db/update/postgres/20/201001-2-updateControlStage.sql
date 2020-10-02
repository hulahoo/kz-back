alter table TSADV_CONTROL_STAGE add column INTEGRATION_USER_LOGIN varchar(255) ;
alter table TSADV_CONTROL_STAGE add column IS_DEFAULT boolean ^
update TSADV_CONTROL_STAGE set IS_DEFAULT = false where IS_DEFAULT is null ;
alter table TSADV_CONTROL_STAGE alter column IS_DEFAULT set not null ;
alter table TSADV_CONTROL_STAGE add column ORGANIZATION_BIN varchar(255) ;
