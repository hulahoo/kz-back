alter table TSADV_PERSON_ACTION_COMMENT rename column person_group_id to person_group_id__u50033 ;
alter table TSADV_PERSON_ACTION_COMMENT alter column person_group_id__u50033 drop not null ;
drop index IDX_TSADV_PERSON_ACTION_COMMENT_PERSON_GROUP ;
alter table TSADV_PERSON_ACTION_COMMENT drop constraint FK_TSADV_PERSON_ACTION_COMMENT_PERSON_GROUP ;
alter table TSADV_PERSON_ACTION_COMMENT rename column like_type_id to like_type_id__u13016 ;
alter table TSADV_PERSON_ACTION_COMMENT alter column like_type_id__u13016 drop not null ;
drop index IDX_TSADV_PERSON_ACTION_COMMENT_LIKE_TYPE ;
alter table TSADV_PERSON_ACTION_COMMENT drop constraint FK_TSADV_PERSON_ACTION_COMMENT_LIKE_TYPE ;
-- alter table TSADV_PERSON_ACTION_COMMENT add column PERSON_GROUP_ID uuid ^
-- update TSADV_PERSON_ACTION_COMMENT set PERSON_GROUP_ID = <default_value> ;
-- alter table TSADV_PERSON_ACTION_COMMENT alter column person_group_id set not null ;
alter table TSADV_PERSON_ACTION_COMMENT add column PERSON_GROUP_ID uuid not null ;
alter table TSADV_PERSON_ACTION_COMMENT add column COMMENT_ varchar(2000) ;
