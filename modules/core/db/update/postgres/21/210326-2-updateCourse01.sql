alter table TSADV_COURSE add constraint FK_TSADV_COURSE_LEARNING_PROOF foreign key (LEARNING_PROOF_ID) references TSADV_DIC_LEARNING_PROOF(ID);
create index IDX_TSADV_COURSE_LEARNING_PROOF on TSADV_COURSE (LEARNING_PROOF_ID);
