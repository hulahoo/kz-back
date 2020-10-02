alter table TSADV_DIC_OPERATOR_CODE add column INTEGRATION_USER_LOGIN varchar(255) ;
alter table TSADV_DIC_OPERATOR_CODE add column IS_DEFAULT boolean ^
update TSADV_DIC_OPERATOR_CODE set IS_DEFAULT = false where IS_DEFAULT is null ;
alter table TSADV_DIC_OPERATOR_CODE alter column IS_DEFAULT set not null ;
alter table TSADV_DIC_OPERATOR_CODE add column ORGANIZATION_BIN varchar(255) ;
