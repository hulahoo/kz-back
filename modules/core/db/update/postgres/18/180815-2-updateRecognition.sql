alter table TSADV_RECOGNITION add column NOTIFY_MANAGER boolean ^
update TSADV_RECOGNITION set NOTIFY_MANAGER = false where NOTIFY_MANAGER is null ;
alter table TSADV_RECOGNITION alter column NOTIFY_MANAGER set not null ;
