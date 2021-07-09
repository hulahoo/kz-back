alter table TSADV_ABSENCE_REQUEST rename column attachment_id to attachment_id__u46721 ;
alter table TSADV_ABSENCE_REQUEST drop constraint FK_TSADV_ABSENCE_REQUEST_ATTACHMENT ;
drop index IDX_TSADV_ABSENCE_REQUEST_ATTACHMENT ;
