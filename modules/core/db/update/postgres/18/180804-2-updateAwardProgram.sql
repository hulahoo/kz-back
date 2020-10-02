alter table TSADV_AWARD_PROGRAM add column YEAR_ integer ^
update TSADV_AWARD_PROGRAM set YEAR_ = 0 where YEAR_ is null ;
alter table TSADV_AWARD_PROGRAM alter column YEAR_ set not null ;
