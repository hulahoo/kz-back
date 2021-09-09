alter table TSADV_ANSWER add constraint FK_TSADV_ANSWER_IMAGE foreign key (IMAGE_ID) references SYS_FILE(ID);
create index IDX_TSADV_ANSWER_IMAGE on TSADV_ANSWER (IMAGE_ID);
