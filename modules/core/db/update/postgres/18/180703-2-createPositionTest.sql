alter table TSADV_POSITION_TEST add constraint FK_TSADV_POSITION_TEST_POSITION_GROUP foreign key (POSITION_GROUP_ID) references BASE_POSITION_GROUP(ID);
alter table TSADV_POSITION_TEST add constraint FK_TSADV_POSITION_TEST_TEST foreign key (TEST_ID) references TSADV_TEST(ID);
create index IDX_TSADV_POSITION_TEST_POSITION_GROUP on TSADV_POSITION_TEST (POSITION_GROUP_ID);
create index IDX_TSADV_POSITION_TEST_TEST on TSADV_POSITION_TEST (TEST_ID);
