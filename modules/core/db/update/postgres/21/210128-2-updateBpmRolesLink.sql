update TSADV_BPM_ROLES_LINK set ORDER_ = 0 where ORDER_ is null ;
alter table TSADV_BPM_ROLES_LINK alter column ORDER_ set not null ;
