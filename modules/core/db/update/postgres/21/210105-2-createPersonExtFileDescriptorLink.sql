alter table TSADV_PERSON_EXT_FILE_DESCRIPTOR_LINK add constraint FK_PEREXTFILDES_PERSON_EXT foreign key (PERSON_EXT_ID) references BASE_PERSON(ID);
alter table TSADV_PERSON_EXT_FILE_DESCRIPTOR_LINK add constraint FK_PEREXTFILDES_FILE_DESCRIPTOR foreign key (FILE_DESCRIPTOR_ID) references SYS_FILE(ID);