alter table TSADV_CERTIFICATE_REQUEST rename column cretificate_type_id to cretificate_type_id__u64447 ;
alter table TSADV_CERTIFICATE_REQUEST alter column cretificate_type_id__u64447 drop not null ;
alter table TSADV_CERTIFICATE_REQUEST drop constraint FK_TSADV_CERTIFICATE_REQUEST_CRETIFICATE_TYPE ;
drop index IDX_TSADV_CERTIFICATE_REQUEST_CRETIFICATE_TYPE ;
 alter table TSADV_CERTIFICATE_REQUEST add column CERTIFICATE_TYPE_ID uuid ^
 update TSADV_CERTIFICATE_REQUEST set CERTIFICATE_TYPE_ID = cretificate_type_id__u64447 ;
 alter table TSADV_CERTIFICATE_REQUEST alter column CERTIFICATE_TYPE_ID set not null ;
--alter table TSADV_CERTIFICATE_REQUEST add column CERTIFICATE_TYPE_ID uuid not null ;
