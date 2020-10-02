-- alter table TSADV_PERSON_ACTION add column AUTHOR_ID uuid ^
-- update TSADV_PERSON_ACTION set AUTHOR_ID = <default_value> ;
-- alter table TSADV_PERSON_ACTION alter column AUTHOR_ID set not null ;
alter table TSADV_PERSON_ACTION add column AUTHOR_ID uuid not null ;
-- alter table TSADV_PERSON_ACTION add column RECEIVER_ID uuid ^
-- update TSADV_PERSON_ACTION set RECEIVER_ID = <default_value> ;
-- alter table TSADV_PERSON_ACTION alter column RECEIVER_ID set not null ;
alter table TSADV_PERSON_ACTION add column RECEIVER_ID uuid not null ;
