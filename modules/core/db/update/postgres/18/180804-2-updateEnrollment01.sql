alter table TSADV_ENROLLMENT add constraint FK_TSADV_ENROLLMENT_REASON_FOR_LEARNING foreign key (REASON_FOR_LEARNING_ID) references TSADV_DIC_REASON_FOR_LEARNING(ID);
create index IDX_TSADV_ENROLLMENT_REASON_FOR_LEARNING on TSADV_ENROLLMENT (REASON_FOR_LEARNING_ID);
