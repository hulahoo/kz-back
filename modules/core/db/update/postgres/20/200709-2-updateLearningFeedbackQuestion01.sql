alter table TSADV_LEARNING_FEEDBACK_QUESTION add constraint FK_TSADV_LEARNING_FEEDBACK_QUESTION_DIC_QUESTION_TYPE foreign key (DIC_QUESTION_TYPE_ID) references TSADV_DIC_LEARNING_FEEDBACK_QUESTION_TYPE(ID);
create index IDX_TSADV_LEARNING_FEEDBACK_QUESTION_DIC_QUESTION_TYPE on TSADV_LEARNING_FEEDBACK_QUESTION (DIC_QUESTION_TYPE_ID);
