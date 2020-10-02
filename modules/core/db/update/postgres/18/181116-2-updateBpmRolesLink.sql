-- update TSADV_BPM_ROLES_LINK set BPM_ROLE_ID = <default_value> where BPM_ROLE_ID is null ;
alter table TSADV_BPM_ROLES_LINK alter column BPM_ROLE_ID set not null ;
-- update TSADV_BPM_ROLES_LINK set HR_ROLE_ID = <default_value> where HR_ROLE_ID is null ;
alter table TSADV_BPM_ROLES_LINK alter column HR_ROLE_ID set not null ;
