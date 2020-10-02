delete from TSADV_TIMECARD_DEVIATION;
alter table TSADV_TIMECARD_DEVIATION rename column timecard_header_id to timecard_header_id__u27775 ;
alter table TSADV_TIMECARD_DEVIATION alter column timecard_header_id__u27775 drop not null ;
drop index IDX_TSADV_TIMECARD_DEVIATION_TIMECARD_HEADER ;
alter table TSADV_TIMECARD_DEVIATION drop constraint FK_TSADV_TIMECARD_DEVIATION_TIMECARD_HEADER ;
-- alter table TSADV_TIMECARD_DEVIATION add column ASSIGNMENT_GROUP_ID uuid ^
-- update TSADV_TIMECARD_DEVIATION set ASSIGNMENT_GROUP_ID = <default_value> ;
-- alter table TSADV_TIMECARD_DEVIATION alter column ASSIGNMENT_GROUP_ID set not null ;
alter table TSADV_TIMECARD_DEVIATION add column ASSIGNMENT_GROUP_ID uuid not null ;
