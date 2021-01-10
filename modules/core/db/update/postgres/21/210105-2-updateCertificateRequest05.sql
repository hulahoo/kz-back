alter table TSADV_CERTIFICATE_REQUEST add constraint FK_TSADV_CERTIFICATE_REQUEST_FILE foreign key (FILE_ID) references SYS_FILE(ID);
