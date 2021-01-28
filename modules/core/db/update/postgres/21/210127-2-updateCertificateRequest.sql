alter table TSADV_CERTIFICATE_REQUEST add column LEGACY_ID varchar(255) ;
alter table TSADV_CERTIFICATE_REQUEST add column INTEGRATION_USER_LOGIN varchar(255) ;
alter table TSADV_CERTIFICATE_REQUEST add column COMMENT_ varchar(3000) ;
alter table TSADV_CERTIFICATE_REQUEST add column ORGANIZATION_BIN varchar(255) ;
alter table TSADV_CERTIFICATE_REQUEST alter column REQUEST_NUMBER drop not null ;
alter table TSADV_CERTIFICATE_REQUEST alter column REQUEST_DATE drop not null ;
alter table TSADV_CERTIFICATE_REQUEST alter column STATUS_ID drop not null ;
