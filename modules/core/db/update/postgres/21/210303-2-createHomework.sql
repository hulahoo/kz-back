alter table TSADV_HOMEWORK add constraint FK_TSADV_HOMEWORK_COURSE foreign key (COURSE_ID) references TSADV_COURSE(ID);
alter table TSADV_HOMEWORK add constraint FK_TSADV_HOMEWORK_INSTRUCTION_FILE foreign key (INSTRUCTION_FILE_ID) references SYS_FILE(ID);
create index IDX_TSADV_HOMEWORK_COURSE on TSADV_HOMEWORK (COURSE_ID);
create index IDX_TSADV_HOMEWORK_INSTRUCTION_FILE on TSADV_HOMEWORK (INSTRUCTION_FILE_ID);