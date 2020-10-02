alter table TSADV_JOB_TEST add constraint FK_TSADV_JOB_TEST_JOB_GROUP foreign key (JOB_GROUP_ID) references TSADV_JOB_GROUP(ID);
alter table TSADV_JOB_TEST add constraint FK_TSADV_JOB_TEST_TEST foreign key (TEST_ID) references TSADV_TEST(ID);
create index IDX_TSADV_JOB_TEST_JOB_GROUP on TSADV_JOB_TEST (JOB_GROUP_ID);
create index IDX_TSADV_JOB_TEST_TEST on TSADV_JOB_TEST (TEST_ID);
