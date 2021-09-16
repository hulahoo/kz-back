alter table TSADV_QUESTION add constraint FK_TSADV_QUESTION_IMAGE foreign key (IMAGE_ID) references SYS_FILE(ID);
create index IDX_TSADV_QUESTION_IMAGE on TSADV_QUESTION (IMAGE_ID);
