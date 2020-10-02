-- alter table TSADV_BPM_ROLES_LINK add column BPM_ROLES_DEFINER_ID uuid ^
-- update TSADV_BPM_ROLES_LINK set BPM_ROLES_DEFINER_ID = <default_value> ;
-- alter table TSADV_BPM_ROLES_LINK alter column BPM_ROLES_DEFINER_ID set not null ;
alter table TSADV_BPM_ROLES_LINK add column BPM_ROLES_DEFINER_ID uuid not null ;
