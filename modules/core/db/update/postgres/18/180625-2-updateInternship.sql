alter table TSADV_INTERNSHIP rename column internship_type_id to internship_type_id__u57888 ;
alter table TSADV_INTERNSHIP alter column internship_type_id__u57888 drop not null ;
drop index IDX_TSADV_INTERNSHIP_INTERNSHIP_TYPE ;
-- alter table TSADV_INTERNSHIP add column INTERNSHIP_TYPE_ID uuid ^
-- update TSADV_INTERNSHIP set INTERNSHIP_TYPE_ID = <default_value> ;
-- alter table TSADV_INTERNSHIP alter column internship_type_id set not null ;
alter table TSADV_INTERNSHIP add column INTERNSHIP_TYPE_ID uuid not null ;
alter table TSADV_INTERNSHIP add column INTERNSHIP_RATING_ID uuid ;
alter table TSADV_INTERNSHIP add column ORGANIZATION_GROUP_ID uuid ;
