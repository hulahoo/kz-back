-- alter table TSADV_VACATION_SCHEDULE add column STATUS_ID uuid ^
-- update TSADV_VACATION_SCHEDULE set STATUS_ID = <default_value> ;
-- alter table TSADV_VACATION_SCHEDULE alter column STATUS_ID set not null ;
alter table TSADV_VACATION_SCHEDULE add column STATUS_ID uuid not null ;
alter table TSADV_VACATION_SCHEDULE add column LEGACY_ID varchar(255) ;
alter table TSADV_VACATION_SCHEDULE add column INTEGRATION_USER_LOGIN varchar(255) ;
alter table TSADV_VACATION_SCHEDULE add column ORGANIZATION_BIN varchar(255) ;
