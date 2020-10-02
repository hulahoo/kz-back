alter table TSADV_GOODS_ISSUE add constraint FK_TSADV_GOODS_ISSUE_GOOD foreign key (GOOD_ID) references TSADV_GOODS(ID);
alter table TSADV_GOODS_ISSUE add constraint FK_TSADV_GOODS_ISSUE_PERSON_GROUP foreign key (PERSON_GROUP_ID) references BASE_PERSON(ID);
create index IDX_TSADV_GOODS_ISSUE_GOOD on TSADV_GOODS_ISSUE (GOOD_ID);
create index IDX_TSADV_GOODS_ISSUE_PERSON_GROUP on TSADV_GOODS_ISSUE (PERSON_GROUP_ID);
