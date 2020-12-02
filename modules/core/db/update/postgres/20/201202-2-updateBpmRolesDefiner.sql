alter table TSADV_BPM_ROLES_DEFINER rename column proc_model_id to proc_model_id__u15829 ;
alter table TSADV_BPM_ROLES_DEFINER alter column proc_model_id__u15829 drop not null ;
alter table TSADV_BPM_ROLES_DEFINER drop constraint FK_TSADV_BPM_ROLES_DEFINER_PROC_MODEL ;
drop index IDX_TSADV_BPM_ROLES_DEFINER_PROC_MODEL ;
drop index IDX_TSADV_BPM_ROLES_DEFINER_UK_PROC_MODEL_ID ;
