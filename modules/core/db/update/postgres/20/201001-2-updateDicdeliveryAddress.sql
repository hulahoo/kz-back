alter table TSADV_DICDELIVERY_ADDRESS add column INTEGRATION_USER_LOGIN varchar(255) ;
alter table TSADV_DICDELIVERY_ADDRESS add column IS_DEFAULT boolean ^
update TSADV_DICDELIVERY_ADDRESS set IS_DEFAULT = false where IS_DEFAULT is null ;
alter table TSADV_DICDELIVERY_ADDRESS alter column IS_DEFAULT set not null ;
alter table TSADV_DICDELIVERY_ADDRESS add column ORGANIZATION_BIN varchar(255) ;
