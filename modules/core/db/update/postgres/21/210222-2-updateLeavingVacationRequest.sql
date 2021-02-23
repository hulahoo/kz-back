alter table TSADV_LEAVING_VACATION_REQUEST rename column status_request_id to status_request_id__u48814 ;
alter table TSADV_LEAVING_VACATION_REQUEST drop constraint FK_TSADV_LEAVING_VACATION_REQUEST_STATUS_REQUEST ;
drop index IDX_TSADV_LEAVING_VACATION_REQUEST_STATUS_REQUEST ;
-- alter table TSADV_LEAVING_VACATION_REQUEST add column STATUS_ID uuid ^
-- update TSADV_LEAVING_VACATION_REQUEST set STATUS_ID = <default_value> ;
-- alter table TSADV_LEAVING_VACATION_REQUEST alter column STATUS_ID set not null ;
alter table TSADV_LEAVING_VACATION_REQUEST add column STATUS_ID uuid not null;
update TSADV_LEAVING_VACATION_REQUEST set REQUEST_DATE = current_date where REQUEST_DATE is null ;
alter table TSADV_LEAVING_VACATION_REQUEST alter column REQUEST_DATE set not null ;
alter table TSADV_LEAVING_VACATION_REQUEST alter column COMMENT_ set data type varchar(3000) ;
