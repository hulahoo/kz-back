alter table TSADV_ASSIGNED_GOAL add column CANT_DELETE boolean ^
update TSADV_ASSIGNED_GOAL set CANT_DELETE = false where CANT_DELETE is null ;
alter table TSADV_ASSIGNED_GOAL alter column CANT_DELETE set not null ;
