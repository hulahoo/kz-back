alter table TSADV_ADDRESS_FILE_DESCRIPTOR_LINK add constraint FK_ADDFILDES_ADDRESS foreign key (ADDRESS_ID) references TSADV_ADDRESS(ID);
alter table TSADV_ADDRESS_FILE_DESCRIPTOR_LINK add constraint FK_ADDFILDES_FILE_DESCRIPTOR foreign key (FILE_DESCRIPTOR_ID) references SYS_FILE(ID);