alter table TSADV_WORKED_HOURS_DETAILED add column PLAN_HOURS double precision ^
update TSADV_WORKED_HOURS_DETAILED set PLAN_HOURS = 0 where PLAN_HOURS is null ;
alter table TSADV_WORKED_HOURS_DETAILED alter column PLAN_HOURS set not null ;