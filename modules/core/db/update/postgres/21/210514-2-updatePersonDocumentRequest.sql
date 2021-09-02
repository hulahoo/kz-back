alter table TSADV_PERSON_DOCUMENT_REQUEST rename column request_status_id to request_status_id__u43942 ;
alter table TSADV_PERSON_DOCUMENT_REQUEST alter column request_status_id__u43942 drop not null ;
alter table TSADV_PERSON_DOCUMENT_REQUEST drop constraint FK_TSADV_PERSON_DOCUMENT_REQUEST_REQUEST_STATUS ;
drop index IDX_TSADV_PERSON_DOCUMENT_REQUEST_REQUEST_STATUS ;
alter table TSADV_PERSON_DOCUMENT_REQUEST rename column status_id to status_id__u35103 ;
alter table TSADV_PERSON_DOCUMENT_REQUEST alter column status_id__u35103 drop not null ;
alter table TSADV_PERSON_DOCUMENT_REQUEST drop constraint FK_TSADV_PERSON_DOCUMENT_REQUEST_STATUS ;
drop index IDX_TSADV_PERSON_DOCUMENT_REQUEST_STATUS ;
-- alter table TSADV_PERSON_DOCUMENT_REQUEST add column STATUS_ID uuid ^
-- update TSADV_PERSON_DOCUMENT_REQUEST set STATUS_ID = <default_value> ;
-- alter table TSADV_PERSON_DOCUMENT_REQUEST alter column status_id set not null ;
alter table TSADV_PERSON_DOCUMENT_REQUEST add column STATUS_ID uuid not null ;
alter table TSADV_PERSON_DOCUMENT_REQUEST add column REQUEST_NUMBER bigint ^
update TSADV_PERSON_DOCUMENT_REQUEST set REQUEST_NUMBER = 0 where REQUEST_NUMBER is null ;
alter table TSADV_PERSON_DOCUMENT_REQUEST alter column REQUEST_NUMBER set not null ;
alter table TSADV_PERSON_DOCUMENT_REQUEST add column REQUEST_DATE date ^
update TSADV_PERSON_DOCUMENT_REQUEST set REQUEST_DATE = current_date where REQUEST_DATE is null ;
alter table TSADV_PERSON_DOCUMENT_REQUEST alter column REQUEST_DATE set not null ;
alter table TSADV_PERSON_DOCUMENT_REQUEST add column COMMENT_ varchar(3000) ;
