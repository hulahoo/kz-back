alter table TSADV_TIMECARD_HEADER add column DAY_HOURS double precision ^
update TSADV_TIMECARD_HEADER set DAY_HOURS = 0 where DAY_HOURS is null ;
alter table TSADV_TIMECARD_HEADER alter column DAY_HOURS set not null ;
