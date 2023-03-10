alter table TSADV_PERSONAL_DATA_REQUEST add constraint FK_TSADV_PERSONAL_DATA_REQUEST_MARITAL_STATUS foreign key (MARITAL_STATUS_ID) references TSADV_DIC_MARITAL_STATUS(ID);
alter table TSADV_PERSONAL_DATA_REQUEST add constraint FK_TSADV_PERSONAL_DATA_REQUEST_ATTACHMENT foreign key (ATTACHMENT_ID) references SYS_FILE(ID);
alter table TSADV_PERSONAL_DATA_REQUEST add constraint FK_TSADV_PERSONAL_DATA_REQUEST_STATUS foreign key (STATUS_ID) references TSADV_DIC_REQUEST_STATUS(ID);
alter table TSADV_PERSONAL_DATA_REQUEST add constraint FK_TSADV_PERSONAL_DATA_REQUEST_PERSON foreign key (PERSON_ID) references BASE_PERSON(ID);
create index IDX_TSADV_PERSONAL_DATA_REQUEST_MARITAL_STATUS on TSADV_PERSONAL_DATA_REQUEST (MARITAL_STATUS_ID);
create index IDX_TSADV_PERSONAL_DATA_REQUEST_ATTACHMENT on TSADV_PERSONAL_DATA_REQUEST (ATTACHMENT_ID);
create index IDX_TSADV_PERSONAL_DATA_REQUEST_STATUS on TSADV_PERSONAL_DATA_REQUEST (STATUS_ID);
