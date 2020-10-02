alter table TSADV_ABSENCE add constraint FK_TSADV_ABSENCE_FILE foreign key (FILE_ID) references SYS_FILE(ID);
create index IDX_TSADV_ABSENCE_FILE on TSADV_ABSENCE (FILE_ID);
