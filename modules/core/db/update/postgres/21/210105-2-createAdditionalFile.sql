alter table TSADV_ADDITIONAL_FILE add constraint FK_TSADV_ADDITIONAL_FILE_FILE foreign key (FILE_ID) references SYS_FILE(ID);
create index IDX_TSADV_ADDITIONAL_FILE_FILE on TSADV_ADDITIONAL_FILE (FILE_ID);
