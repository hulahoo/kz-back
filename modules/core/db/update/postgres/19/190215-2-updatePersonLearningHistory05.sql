alter table TSADV_PERSON_LEARNING_HISTORY add constraint FK_TSADV_PERSON_LEARNING_HISTORY_LEARNING_TYPE foreign key (LEARNING_TYPE_ID) references TSADV_DIC_LEARNING_TYPE(ID);
create index IDX_TSADV_PERSON_LEARNING_HISTORY_LEARNING_TYPE on TSADV_PERSON_LEARNING_HISTORY (LEARNING_TYPE_ID);
