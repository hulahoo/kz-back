alter table TSADV_PERSON_EXPERIENCE_REQUEST_FILE_DESCRIPTOR_LINK add constraint FK_PEREXPREQFILDES_PERSON_EXPERIENCE_REQUEST foreign key (PERSON_EXPERIENCE_REQUEST_ID) references TSADV_PERSON_EXPERIENCE_REQUEST(ID);
alter table TSADV_PERSON_EXPERIENCE_REQUEST_FILE_DESCRIPTOR_LINK add constraint FK_PEREXPREQFILDES_FILE_DESCRIPTOR foreign key (FILE_DESCRIPTOR_ID) references SYS_FILE(ID);