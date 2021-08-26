alter table BASE_PERSON_GROUP add constraint FK_BASE_PERSON_GROUP_IMAGE foreign key (IMAGE_ID) references SYS_FILE(ID);
