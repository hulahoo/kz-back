alter table TSADV_MILITARY_FORM_FILE_DESCRIPTOR_LINK add constraint FK_MILFORFILDES_MILITARY_FORM foreign key (MILITARY_FORM_ID) references TSADV_MILITARY_FORM(ID);
alter table TSADV_MILITARY_FORM_FILE_DESCRIPTOR_LINK add constraint FK_MILFORFILDES_FILE_DESCRIPTOR foreign key (FILE_DESCRIPTOR_ID) references SYS_FILE(ID);