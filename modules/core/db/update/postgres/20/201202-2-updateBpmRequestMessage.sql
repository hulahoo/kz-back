alter table TSADV_BPM_REQUEST_MESSAGE rename column proc_instance_id to proc_instance_id__u32809 ;
alter table TSADV_BPM_REQUEST_MESSAGE drop constraint FK_TSADV_BPM_REQUEST_MESSAGE_PROC_INSTANCE ;
drop index IDX_TSADV_BPM_REQUEST_MESSAGE_PROC_INSTANCE ;