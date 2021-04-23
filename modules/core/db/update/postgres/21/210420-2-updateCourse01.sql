alter table TSADV_COURSE add constraint FK_TSADV_COURSE_LOGO foreign key (LOGO_ID) references SYS_FILE(ID);
create index IDX_TSADV_COURSE_LOGO on TSADV_COURSE (LOGO_ID);
