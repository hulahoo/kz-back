alter table TSADV_BPM_ROLES_LINK rename column bpm_role_id to bpm_role_id__u99390 ;
alter table TSADV_BPM_ROLES_LINK alter column bpm_role_id__u99390 drop not null ;
alter table TSADV_BPM_ROLES_LINK drop constraint FK_TSADV_BPM_ROLES_LINK_BPM_ROLE ;
drop index IDX_TSADV_BPM_ROLES_LINK_BPM_ROLE ;
