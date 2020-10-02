alter table TSADV_DIC_LC_ARTICLE add column INTEGRATION_USER_LOGIN varchar(255) ;
alter table TSADV_DIC_LC_ARTICLE add column IS_DEFAULT boolean ^
update TSADV_DIC_LC_ARTICLE set IS_DEFAULT = false where IS_DEFAULT is null ;
alter table TSADV_DIC_LC_ARTICLE alter column IS_DEFAULT set not null ;
alter table TSADV_DIC_LC_ARTICLE add column ORGANIZATION_BIN varchar(255) ;
