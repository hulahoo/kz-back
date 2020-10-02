alter table TSADV_DIC_MILITARY_RANK add column INTEGRATION_USER_LOGIN varchar(255) ;
alter table TSADV_DIC_MILITARY_RANK add column IS_DEFAULT boolean ^
update TSADV_DIC_MILITARY_RANK set IS_DEFAULT = false where IS_DEFAULT is null ;
alter table TSADV_DIC_MILITARY_RANK alter column IS_DEFAULT set not null ;
alter table TSADV_DIC_MILITARY_RANK add column ORGANIZATION_BIN varchar(255) ;
