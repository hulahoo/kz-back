alter table TSADV_BPM_ROLES_LINK add column IS_ADDABLE_APPROVER boolean ^
update TSADV_BPM_ROLES_LINK set IS_ADDABLE_APPROVER = false where IS_ADDABLE_APPROVER is null ;
alter table TSADV_BPM_ROLES_LINK alter column IS_ADDABLE_APPROVER set not null ;
