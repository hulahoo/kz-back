alter table TSADV_BPM_REQUEST_MESSAGE rename column assigned_by_id to assigned_by_id__u78953 ;
drop index IDX_TSADV_BPM_REQUEST_MESSAGE_ASSIGNED_BY ;
alter table TSADV_BPM_REQUEST_MESSAGE rename column assigned_user_id to assigned_user_id__u20494 ;
drop index IDX_TSADV_BPM_REQUEST_MESSAGE_ASSIGNED_USER ;
alter table TSADV_BPM_REQUEST_MESSAGE add column ASSIGNED_USER_ID uuid ;
alter table TSADV_BPM_REQUEST_MESSAGE add column ASSIGNED_BY_ID uuid ;
