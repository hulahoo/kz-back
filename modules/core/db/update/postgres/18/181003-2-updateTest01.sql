alter table TSADV_TEST add constraint FK_TSADV_TEST_COURSE foreign key (COURSE_ID) references TSADV_COURSE(ID);
create index IDX_TSADV_TEST_COURSE on TSADV_TEST (COURSE_ID);
