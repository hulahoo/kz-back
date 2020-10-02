alter table TSADV_ASSIGNMENT_REQUEST rename column reason_id to reason_id__u06945 ;
drop index IDX_TSADV_ASSIGNMENT_REQUEST_REASON ;
alter table TSADV_ASSIGNMENT_REQUEST drop constraint FK_TSADV_ASSIGNMENT_REQUEST_REASON ;
alter table TSADV_ASSIGNMENT_REQUEST add column ATTACHMENT_ID uuid ;
