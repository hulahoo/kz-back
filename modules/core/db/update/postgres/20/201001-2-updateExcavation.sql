alter table TSADV_EXCAVATION add column INTEGRATION_USER_LOGIN varchar(255) ;
alter table TSADV_EXCAVATION add column IS_DEFAULT boolean ^
update TSADV_EXCAVATION set IS_DEFAULT = false where IS_DEFAULT is null ;
alter table TSADV_EXCAVATION alter column IS_DEFAULT set not null ;
alter table TSADV_EXCAVATION add column ORGANIZATION_BIN varchar(255) ;
