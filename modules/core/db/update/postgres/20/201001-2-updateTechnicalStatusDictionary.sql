alter table TSADV_TECHNICAL_STATUS_DICTIONARY add column INTEGRATION_USER_LOGIN varchar(255) ;
alter table TSADV_TECHNICAL_STATUS_DICTIONARY add column IS_DEFAULT boolean ^
update TSADV_TECHNICAL_STATUS_DICTIONARY set IS_DEFAULT = false where IS_DEFAULT is null ;
alter table TSADV_TECHNICAL_STATUS_DICTIONARY alter column IS_DEFAULT set not null ;
alter table TSADV_TECHNICAL_STATUS_DICTIONARY add column ORGANIZATION_BIN varchar(255) ;
