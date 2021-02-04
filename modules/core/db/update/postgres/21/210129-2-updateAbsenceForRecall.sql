alter table TSADV_ABSENCE_FOR_RECALL add column COMMENT_ varchar(3000) ;
update TSADV_ABSENCE_FOR_RECALL set REQUEST_NUMBER = 0 where REQUEST_NUMBER is null ;
alter table TSADV_ABSENCE_FOR_RECALL alter column REQUEST_NUMBER set not null ;
-- update TSADV_ABSENCE_FOR_RECALL set STATUS_ID = <default_value> where STATUS_ID is null ;
alter table TSADV_ABSENCE_FOR_RECALL alter column STATUS_ID set not null ;
update TSADV_ABSENCE_FOR_RECALL set REQUEST_DATE = current_date where REQUEST_DATE is null ;
alter table TSADV_ABSENCE_FOR_RECALL alter column REQUEST_DATE set not null ;
