alter table TSADV_DIC_NATIONALITY add column INTEGRATION_USER_LOGIN varchar(255) ;
alter table TSADV_DIC_NATIONALITY add column IS_DEFAULT boolean ^
update TSADV_DIC_NATIONALITY set IS_DEFAULT = false where IS_DEFAULT is null ;
alter table TSADV_DIC_NATIONALITY alter column IS_DEFAULT set not null ;
alter table TSADV_DIC_NATIONALITY add column ORGANIZATION_BIN varchar(255) ;
