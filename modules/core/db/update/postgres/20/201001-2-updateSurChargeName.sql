alter table TSADV_SUR_CHARGE_NAME add column INTEGRATION_USER_LOGIN varchar(255) ;
alter table TSADV_SUR_CHARGE_NAME add column IS_DEFAULT boolean ^
update TSADV_SUR_CHARGE_NAME set IS_DEFAULT = false where IS_DEFAULT is null ;
alter table TSADV_SUR_CHARGE_NAME alter column IS_DEFAULT set not null ;
alter table TSADV_SUR_CHARGE_NAME add column ORGANIZATION_BIN varchar(255) ;
