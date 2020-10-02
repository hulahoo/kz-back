alter table TSADV_BPM_ROLES_LINK add column FIND_BY_COUNTER boolean ^
update TSADV_BPM_ROLES_LINK set FIND_BY_COUNTER = (lower(r.code) = 'admin_approve' or lower(r.code) = 'hr_specialist' or lower(r.code) = 'hr_specialist_front') from TSADV_DIC_HR_ROLE r where r.id = TSADV_BPM_ROLES_LINK.HR_ROLE_ID;
alter table TSADV_BPM_ROLES_LINK alter column FIND_BY_COUNTER set not null ;
