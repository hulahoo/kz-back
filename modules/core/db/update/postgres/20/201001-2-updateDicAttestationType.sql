alter table TSADV_DIC_ATTESTATION_TYPE add column INTEGRATION_USER_LOGIN varchar(255) ;
alter table TSADV_DIC_ATTESTATION_TYPE add column IS_DEFAULT boolean ^
update TSADV_DIC_ATTESTATION_TYPE set IS_DEFAULT = false where IS_DEFAULT is null ;
alter table TSADV_DIC_ATTESTATION_TYPE alter column IS_DEFAULT set not null ;
alter table TSADV_DIC_ATTESTATION_TYPE add column ORGANIZATION_BIN varchar(255) ;
