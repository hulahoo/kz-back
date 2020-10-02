alter table TSADV_INTERNSHIP rename column trainee_id to PERSON_GROUP_ID ;
drop index IDX_TSADV_INTERNSHIP_TRAINEE ;
alter table TSADV_INTERNSHIP drop constraint FK_TSADV_INTERNSHIP_TRAINEE ;
-- alter table TSADV_INTERNSHIP add column PERSON_GROUP_ID uuid ^
-- update TSADV_INTERNSHIP set PERSON_GROUP_ID = <default_value> ;
-- alter table TSADV_INTERNSHIP alter column PERSON_GROUP_ID set not null ;
