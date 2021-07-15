alter table TSADV_ADDRESS_REQUEST rename column attachment_id to attachment_id__u26986 ;
alter table TSADV_ADDRESS_REQUEST drop constraint FK_TSADV_ADDRESS_REQUEST_ATTACHMENT ;
drop index IDX_TSADV_ADDRESS_REQUEST_ATTACHMENT ;
alter table TSADV_ADDRESS_REQUEST add column if not exists LEGACY_ID varchar(255) ;
alter table TSADV_ADDRESS_REQUEST add column if not exists INTEGRATION_USER_LOGIN varchar(255) ;
alter table TSADV_ADDRESS_REQUEST add column if not exists REQUEST_DATE date ^
update TSADV_ADDRESS_REQUEST set REQUEST_DATE = current_date where REQUEST_DATE is null ;
alter table TSADV_ADDRESS_REQUEST alter column REQUEST_DATE set not null ;
alter table TSADV_ADDRESS_REQUEST add column if not exists COMMENT_ varchar(3000) ;
alter table TSADV_ADDRESS_REQUEST add column if not exists ORGANIZATION_BIN varchar(255) ;
update TSADV_ADDRESS_REQUEST set REQUEST_NUMBER = 0 where REQUEST_NUMBER is null ;
alter table TSADV_ADDRESS_REQUEST alter column REQUEST_NUMBER set not null ;
-- update TSADV_ADDRESS_REQUEST set STATUS_ID = <default_value> where STATUS_ID is null ;
alter table TSADV_ADDRESS_REQUEST alter column STATUS_ID set not null;
