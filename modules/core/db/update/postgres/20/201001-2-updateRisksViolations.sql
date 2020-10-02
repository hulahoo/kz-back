alter table TSADV_RISKS_VIOLATIONS add column INTEGRATION_USER_LOGIN varchar(255) ;
alter table TSADV_RISKS_VIOLATIONS add column IS_DEFAULT boolean ^
update TSADV_RISKS_VIOLATIONS set IS_DEFAULT = false where IS_DEFAULT is null ;
alter table TSADV_RISKS_VIOLATIONS alter column IS_DEFAULT set not null ;
alter table TSADV_RISKS_VIOLATIONS add column ORGANIZATION_BIN varchar(255) ;
