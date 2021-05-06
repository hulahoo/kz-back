-- alter table TSADV_SCHEDULE_OFFSETS_REQUEST add column EARNING_POLICY_ID uuid ^
-- update TSADV_SCHEDULE_OFFSETS_REQUEST set EARNING_POLICY_ID = <default_value> ;
-- alter table TSADV_SCHEDULE_OFFSETS_REQUEST alter column EARNING_POLICY_ID set not null ;
alter table TSADV_SCHEDULE_OFFSETS_REQUEST add column EARNING_POLICY_ID uuid not null ;
update TSADV_SCHEDULE_OFFSETS_REQUEST set AGREE = false where AGREE is null ;
alter table TSADV_SCHEDULE_OFFSETS_REQUEST alter column AGREE set not null ;
update TSADV_SCHEDULE_OFFSETS_REQUEST set ACQUAINTED = false where ACQUAINTED is null ;
alter table TSADV_SCHEDULE_OFFSETS_REQUEST alter column ACQUAINTED set not null ;
