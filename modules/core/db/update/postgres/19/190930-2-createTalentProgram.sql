alter table TSADV_TALENT_PROGRAM add constraint FK_TSADV_TALENT_PROGRAM_BANNER_LANG_1 foreign key (BANNER_LANG_1_ID) references SYS_FILE(ID);
alter table TSADV_TALENT_PROGRAM add constraint FK_TSADV_TALENT_PROGRAM_BANNER_LANG_2 foreign key (BANNER_LANG_2_ID) references SYS_FILE(ID);
alter table TSADV_TALENT_PROGRAM add constraint FK_TSADV_TALENT_PROGRAM_BANNER_LANG_3 foreign key (BANNER_LANG_3_ID) references SYS_FILE(ID);
