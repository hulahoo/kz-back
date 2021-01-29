alter table TSADV_SCHEDULE_OFFSETS_REQUEST add column COMMENT_ varchar(3000) ;
update TSADV_SCHEDULE_OFFSETS_REQUEST set REQUEST_NUMBER = 0 where REQUEST_NUMBER is null ;
alter table TSADV_SCHEDULE_OFFSETS_REQUEST alter column REQUEST_NUMBER set not null ;
-- update TSADV_SCHEDULE_OFFSETS_REQUEST set STATUS_ID = <default_value> where STATUS_ID is null ;
alter table TSADV_SCHEDULE_OFFSETS_REQUEST alter column STATUS_ID set not null ;
update TSADV_SCHEDULE_OFFSETS_REQUEST set REQUEST_DATE = current_date where REQUEST_DATE is null ;
alter table TSADV_SCHEDULE_OFFSETS_REQUEST alter column REQUEST_DATE set not null ;
