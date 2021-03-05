alter table TSADV_STUDENT_HOMEWORK add constraint FK_TSADV_STUDENT_HOMEWORK_HOMEWORK foreign key (HOMEWORK_ID) references TSADV_HOMEWORK(ID);
alter table TSADV_STUDENT_HOMEWORK add constraint FK_TSADV_STUDENT_HOMEWORK_PERSON_GROUP foreign key (PERSON_GROUP_ID) references BASE_PERSON_GROUP(ID);
alter table TSADV_STUDENT_HOMEWORK add constraint FK_TSADV_STUDENT_HOMEWORK_ANSWER_FILE foreign key (ANSWER_FILE_ID) references SYS_FILE(ID);
alter table TSADV_STUDENT_HOMEWORK add constraint FK_TSADV_STUDENT_HOMEWORK_TRAINER foreign key (TRAINER_ID) references BASE_PERSON_GROUP(ID);
create index IDX_TSADV_STUDENT_HOMEWORK_HOMEWORK on TSADV_STUDENT_HOMEWORK (HOMEWORK_ID);
create index IDX_TSADV_STUDENT_HOMEWORK_PERSON_GROUP on TSADV_STUDENT_HOMEWORK (PERSON_GROUP_ID);
create index IDX_TSADV_STUDENT_HOMEWORK_ANSWER_FILE on TSADV_STUDENT_HOMEWORK (ANSWER_FILE_ID);
create index IDX_TSADV_STUDENT_HOMEWORK_TRAINER on TSADV_STUDENT_HOMEWORK (TRAINER_ID);