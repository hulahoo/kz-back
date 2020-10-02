alter table TSADV_PERSON_COIN_LOG add constraint FK_TSADV_PERSON_COIN_LOG_PERSON_GROUP foreign key (PERSON_GROUP_ID) references BASE_PERSON_GROUP(ID);
alter table TSADV_PERSON_COIN_LOG add constraint FK_TSADV_PERSON_COIN_LOG_RECOGNITION foreign key (RECOGNITION_ID) references TSADV_RECOGNITION(ID);
alter table TSADV_PERSON_COIN_LOG add constraint FK_TSADV_PERSON_COIN_LOG_GOODS foreign key (GOODS_ID) references TSADV_GOODS(ID);
create index IDX_TSADV_PERSON_COIN_LOG_PERSON_GROUP on TSADV_PERSON_COIN_LOG (PERSON_GROUP_ID);
create index IDX_TSADV_PERSON_COIN_LOG_RECOGNITION on TSADV_PERSON_COIN_LOG (RECOGNITION_ID);
create index IDX_TSADV_PERSON_COIN_LOG_GOODS on TSADV_PERSON_COIN_LOG (GOODS_ID);
