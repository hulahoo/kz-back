alter table TSADV_HIRING_STEP add constraint FK_TSADV_HIRING_STEP_TEST foreign key (TEST_ID) references TSADV_TEST(ID);
create index IDX_TSADV_HIRING_STEP_TEST on TSADV_HIRING_STEP (TEST_ID);
