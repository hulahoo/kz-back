alter table TSADV_PERSON_DOCUMENT_REQUEST rename column status_id to status_id__u43978 ;
alter table TSADV_PERSON_DOCUMENT_REQUEST alter column status_id__u43978 drop not null ;
alter table TSADV_PERSON_DOCUMENT_REQUEST drop constraint FK_TSADV_PERSON_DOCUMENT_REQUEST_STATUS ;
drop index IDX_TSADV_PERSON_DOCUMENT_REQUEST_STATUS ;
alter table TSADV_PERSON_DOCUMENT_REQUEST rename column file_id to file_id__u07576 ;
alter table TSADV_PERSON_DOCUMENT_REQUEST drop constraint FK_TSADV_PERSON_DOCUMENT_REQUEST_FILE ;
drop index IDX_TSADV_PERSON_DOCUMENT_REQUEST_FILE ;
