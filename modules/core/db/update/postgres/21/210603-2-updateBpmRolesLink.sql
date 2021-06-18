alter table TSADV_BPM_ROLES_LINK add column FOR_ASSISTANT boolean ^
update TSADV_BPM_ROLES_LINK set FOR_ASSISTANT = false where FOR_ASSISTANT is null ;
alter table TSADV_BPM_ROLES_LINK alter column FOR_ASSISTANT set not null ;
