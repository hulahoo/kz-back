alter table TSADV_PERSONAL_DATA_REQUEST rename column attachment_id to attachment_id__u81948 ;
alter table TSADV_PERSONAL_DATA_REQUEST drop constraint FK_TSADV_PERSONAL_DATA_REQUEST_ATTACHMENT ;
drop index IDX_TSADV_PERSONAL_DATA_REQUEST_ATTACHMENT ;
alter table TSADV_PERSONAL_DATA_REQUEST add column REQUEST_DATE date ^
update TSADV_PERSONAL_DATA_REQUEST set REQUEST_DATE = current_date where REQUEST_DATE is null ;
alter table TSADV_PERSONAL_DATA_REQUEST alter column REQUEST_DATE set not null ;
alter table TSADV_PERSONAL_DATA_REQUEST add column COMMENT_ varchar(3000) ;
update TSADV_PERSONAL_DATA_REQUEST set REQUEST_NUMBER = 0 where REQUEST_NUMBER is null ;
alter table TSADV_PERSONAL_DATA_REQUEST alter column REQUEST_NUMBER set not null ;
-- update TSADV_PERSONAL_DATA_REQUEST set STATUS_ID = <default_value> where STATUS_ID is null ;
alter table TSADV_PERSONAL_DATA_REQUEST alter column STATUS_ID set not null ;
