alter table TSADV_PERSON_LEARNING_HISTORY add constraint FK_TSADV_PERSON_LEARNING_HISTORY_ENROLLMENT foreign key (ENROLLMENT_ID) references TSADV_ENROLLMENT(ID);
create index IDX_TSADV_PERSON_LEARNING_HISTORY_ENROLLMENT on TSADV_PERSON_LEARNING_HISTORY (ENROLLMENT_ID);
