alter table TSADV_PERSON_QUALIFICATION_REQUEST_FILE_DESCRIPTOR_LINK add constraint FK_PERQUAREQFILDES_PERSON_QUALIFICATION_REQUEST foreign key (PERSON_QUALIFICATION_REQUEST_ID) references TSADV_PERSON_QUALIFICATION_REQUEST(ID);
alter table TSADV_PERSON_QUALIFICATION_REQUEST_FILE_DESCRIPTOR_LINK add constraint FK_PERQUAREQFILDES_FILE_DESCRIPTOR foreign key (FILE_DESCRIPTOR_ID) references SYS_FILE(ID);