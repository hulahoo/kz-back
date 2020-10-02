alter table TSADV_DISABILITY_GROUP add column INTEGRATION_USER_LOGIN varchar(255) ;
alter table TSADV_DISABILITY_GROUP add column IS_DEFAULT boolean ^
update TSADV_DISABILITY_GROUP set IS_DEFAULT = false where IS_DEFAULT is null ;
alter table TSADV_DISABILITY_GROUP alter column IS_DEFAULT set not null ;
alter table TSADV_DISABILITY_GROUP add column ORGANIZATION_BIN varchar(255) ;
