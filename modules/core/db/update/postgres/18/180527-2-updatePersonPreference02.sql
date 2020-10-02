alter table TSADV_PERSON_PREFERENCE rename column person_group_id to person_group_id__u09749 ;
drop index IDX_TSADV_PERSON_PREFERENCE_PERSON_GROUP ;
alter table TSADV_PERSON_PREFERENCE drop constraint FK_TSADV_PERSON_PREFERENCE_PERSON_GROUP ;
alter table TSADV_PERSON_PREFERENCE rename column preference_type_id to preference_type_id__u10180 ;
drop index IDX_TSADV_PERSON_PREFERENCE_PREFERENCE_TYPE ;
-- alter table TSADV_PERSON_PREFERENCE add column PREFERENCE_TYPE_ID uuid ^
-- update TSADV_PERSON_PREFERENCE set PREFERENCE_TYPE_ID = <default_value> ;
-- alter table TSADV_PERSON_PREFERENCE alter column preference_type_id set not null ;
alter table TSADV_PERSON_PREFERENCE add column PREFERENCE_TYPE_ID uuid not null ;
alter table TSADV_PERSON_PREFERENCE add column PERSON_GROUP_ID uuid ;
-- update TSADV_PERSON_PREFERENCE set PREFERENCE_TYPE_ID = <default_value> where PREFERENCE_TYPE_ID is null ;
alter table TSADV_PERSON_PREFERENCE alter column PREFERENCE_TYPE_ID set not null ;
