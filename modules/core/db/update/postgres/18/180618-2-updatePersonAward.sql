-- alter table TSADV_PERSON_AWARD add column AWARD_PROGRAM_ID uuid ^
-- update TSADV_PERSON_AWARD set AWARD_PROGRAM_ID = <default_value> ;
-- alter table TSADV_PERSON_AWARD alter column AWARD_PROGRAM_ID set not null ;
alter table TSADV_PERSON_AWARD add column AWARD_PROGRAM_ID uuid not null ;
