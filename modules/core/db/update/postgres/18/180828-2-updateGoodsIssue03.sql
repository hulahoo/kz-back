alter table TSADV_GOODS_ISSUE rename column person_group_id to person_group_id__u51544 ;
alter table TSADV_GOODS_ISSUE alter column person_group_id__u51544 drop not null ;
drop index IDX_TSADV_GOODS_ISSUE_PERSON_GROUP ;
alter table TSADV_GOODS_ISSUE drop constraint FK_TSADV_GOODS_ISSUE_PERSON_GROUP ;
alter table TSADV_GOODS_ISSUE rename column status to status__u86692 ;
alter table TSADV_GOODS_ISSUE alter column status__u86692 drop not null ;
alter table TSADV_GOODS_ISSUE add column ORDER_ID uuid ;
