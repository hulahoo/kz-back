alter table TSADV_LEAVING_VACATION_REQUEST rename column status_request_id to status_request_id__u03495 ;
alter table TSADV_LEAVING_VACATION_REQUEST drop constraint FK_TSADV_LEAVING_VACATION_REQUEST_STATUS_REQUEST ;
drop index IDX_TSADV_LEAVING_VACATION_REQUEST_STATUS_REQUEST ;
alter table TSADV_LEAVING_VACATION_REQUEST rename column request_number to request_number__u78316 ;
alter table TSADV_LEAVING_VACATION_REQUEST alter column request_number__u78316 drop not null ;
alter table TSADV_LEAVING_VACATION_REQUEST add column STATUS_REQUEST_ID uuid ;
alter table TSADV_LEAVING_VACATION_REQUEST add column LEGACY_ID varchar(255) ;
alter table TSADV_LEAVING_VACATION_REQUEST add column INTEGRATION_USER_LOGIN varchar(255) ;
alter table TSADV_LEAVING_VACATION_REQUEST add column ORGANIZATION_BIN varchar(255) ;
alter table TSADV_LEAVING_VACATION_REQUEST add column REQUEST_NUMBER bigint ^
update TSADV_LEAVING_VACATION_REQUEST set REQUEST_NUMBER = 0 where REQUEST_NUMBER is null ;
alter table TSADV_LEAVING_VACATION_REQUEST alter column REQUEST_NUMBER set not null ;
