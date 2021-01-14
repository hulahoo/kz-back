-- alter table TSADV_PERSON_DOCUMENT add column ISSUING_AUTHORITY_ID uuid ^
-- update TSADV_PERSON_DOCUMENT set ISSUING_AUTHORITY_ID = <default_value> ;
-- alter table TSADV_PERSON_DOCUMENT alter column ISSUING_AUTHORITY_ID set not null ;
alter table TSADV_PERSON_DOCUMENT add column ISSUING_AUTHORITY_ID uuid not null ;
alter table TSADV_PERSON_DOCUMENT add column END_DATE date ;
alter table TSADV_PERSON_DOCUMENT add column START_DATE date ;
