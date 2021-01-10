alter table TSADV_CERTIFICATE_REQUEST rename column status_id to status_id__u23818 ;
alter table TSADV_CERTIFICATE_REQUEST alter column status_id__u23818 drop not null;
drop index if exists IDX_TSADV_CERTIFICATE_REQUEST_STATUS ;
-- alter table TSADV_CERTIFICATE_REQUEST add column STATUS_ID uuid ^
-- update TSADV_CERTIFICATE_REQUEST set STATUS_ID = <default_value> ;
-- alter table TSADV_CERTIFICATE_REQUEST alter column status_id set not null ;
alter table TSADV_CERTIFICATE_REQUEST add column STATUS_ID uuid not null ;