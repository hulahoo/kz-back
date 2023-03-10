alter table TSADV_GOODS_WISH_LIST add constraint FK_TSADV_GOODS_WISH_LIST_PERSON_GROUP foreign key (PERSON_GROUP_ID) references BASE_PERSON_GROUP(ID);
alter table TSADV_GOODS_WISH_LIST add constraint FK_TSADV_GOODS_WISH_LIST_GOODS foreign key (GOODS_ID) references TSADV_GOODS(ID);
create index IDX_TSADV_GOODS_WISH_LIST_PERSON_GROUP on TSADV_GOODS_WISH_LIST (PERSON_GROUP_ID);
create index IDX_TSADV_GOODS_WISH_LIST_GOODS on TSADV_GOODS_WISH_LIST (GOODS_ID);
