alter table TSADV_DIC_BUDGET_ITEM add column INTEGRATION_USER_LOGIN varchar(255) ;
alter table TSADV_DIC_BUDGET_ITEM add column IS_DEFAULT boolean ^
update TSADV_DIC_BUDGET_ITEM set IS_DEFAULT = false where IS_DEFAULT is null ;
alter table TSADV_DIC_BUDGET_ITEM alter column IS_DEFAULT set not null ;
alter table TSADV_DIC_BUDGET_ITEM add column ORGANIZATION_BIN varchar(255) ;
