alter table TSADV_COURSE add constraint FK_TSADV_COURSE_TYPE_OF_TRAINING foreign key (TYPE_OF_TRAINING_ID) references TSADV_DIC_TYPE_OF_TRAINING(ID);
create index IDX_TSADV_COURSE_TYPE_OF_TRAINING on TSADV_COURSE (TYPE_OF_TRAINING_ID);
