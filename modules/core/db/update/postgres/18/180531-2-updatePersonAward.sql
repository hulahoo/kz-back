alter table TSADV_PERSON_AWARD rename column type_id to type_id__u07960 ;
alter table TSADV_PERSON_AWARD alter column type_id__u07960 drop not null ;
drop index IDX_TSADV_PERSON_AWARD_TYPE ;
alter table TSADV_PERSON_AWARD drop constraint FK_TSADV_PERSON_AWARD_TYPE ;
-- alter table TSADV_PERSON_AWARD add column TYPE_ID uuid ^
-- update TSADV_PERSON_AWARD set TYPE_ID = <default_value> ;
-- alter table TSADV_PERSON_AWARD alter column type_id set not null ;
alter table TSADV_PERSON_AWARD add column TYPE_ID uuid not null ;
