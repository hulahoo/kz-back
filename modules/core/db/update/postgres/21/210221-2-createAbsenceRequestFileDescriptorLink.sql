alter table TSADV_ABSENCE_REQUEST_FILE_DESCRIPTOR_LINK add constraint FK_ABSREQFILDES_ABSENCE_REQUEST foreign key (ABSENCE_REQUEST_ID) references TSADV_ABSENCE_REQUEST(ID);
alter table TSADV_ABSENCE_REQUEST_FILE_DESCRIPTOR_LINK add constraint FK_ABSREQFILDES_FILE_DESCRIPTOR foreign key (FILE_DESCRIPTOR_ID) references SYS_FILE(ID);