alter table TSADV_BPM_ROLES_LINK add column REQUIRED boolean ^
update TSADV_BPM_ROLES_LINK set REQUIRED = false where REQUIRED is null ;
alter table TSADV_BPM_ROLES_LINK alter column REQUIRED set not null ;
