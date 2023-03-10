alter table TSADV_RCG_QUESTION_ANSWER add constraint FK_TSADV_RCG_QUESTION_ANSWER_ICON foreign key (ICON_ID) references SYS_FILE(ID);
alter table TSADV_RCG_QUESTION_ANSWER add constraint FK_TSADV_RCG_QUESTION_ANSWER_RCG_QUESTION foreign key (RCG_QUESTION_ID) references TSADV_RCG_QUESTION(ID);
create index IDX_TSADV_RCG_QUESTION_ANSWER_ICON on TSADV_RCG_QUESTION_ANSWER (ICON_ID);
create index IDX_TSADV_RCG_QUESTION_ANSWER_RCG_QUESTION on TSADV_RCG_QUESTION_ANSWER (RCG_QUESTION_ID);
