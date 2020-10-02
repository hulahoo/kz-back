alter table TSADV_DIC_TROOPS_STRUCTURE add column INTEGRATION_USER_LOGIN varchar(255) ;
alter table TSADV_DIC_TROOPS_STRUCTURE add column IS_DEFAULT boolean ^
update TSADV_DIC_TROOPS_STRUCTURE set IS_DEFAULT = false where IS_DEFAULT is null ;
alter table TSADV_DIC_TROOPS_STRUCTURE alter column IS_DEFAULT set not null ;
alter table TSADV_DIC_TROOPS_STRUCTURE add column ORGANIZATION_BIN varchar(255) ;
