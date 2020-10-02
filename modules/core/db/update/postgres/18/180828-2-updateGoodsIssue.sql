alter table TSADV_GOODS_ISSUE rename column person_group_id to person_group_id__u90241 ;
alter table TSADV_GOODS_ISSUE alter column person_group_id__u90241 drop not null ;
drop index IDX_TSADV_GOODS_ISSUE_PERSON_GROUP ;
alter table TSADV_GOODS_ISSUE drop constraint FK_TSADV_GOODS_ISSUE_PERSON_GROUP ;
alter table TSADV_GOODS_ISSUE rename column good_id to good_id__u16088 ;
alter table TSADV_GOODS_ISSUE alter column good_id__u16088 drop not null ;
drop index IDX_TSADV_GOODS_ISSUE_GOOD ;
alter table TSADV_GOODS_ISSUE drop constraint FK_TSADV_GOODS_ISSUE_GOOD ;
-- alter table TSADV_GOODS_ISSUE add column PERSON_GROUP_ID uuid ^
-- update TSADV_GOODS_ISSUE set PERSON_GROUP_ID = <default_value> ;
-- alter table TSADV_GOODS_ISSUE alter column person_group_id set not null ;
alter table TSADV_GOODS_ISSUE add column PERSON_GROUP_ID uuid not null ;
-- alter table TSADV_GOODS_ISSUE add column GOODS_ID uuid ^
-- update TSADV_GOODS_ISSUE set GOODS_ID = <default_value> ;
-- alter table TSADV_GOODS_ISSUE alter column GOODS_ID set not null ;
alter table TSADV_GOODS_ISSUE add column GOODS_ID uuid not null ;
alter table TSADV_GOODS_ISSUE alter column QUANTITY set data type bigint ;
