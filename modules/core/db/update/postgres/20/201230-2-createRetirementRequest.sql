alter table TSADV_RETIREMENT_REQUEST add constraint FK_TSADV_RETIREMENT_REQUEST_RETIREMENT_TYPE foreign key (RETIREMENT_TYPE_ID) references TSADV_DIC_RETIREMENT_TYPE(ID);
alter table TSADV_RETIREMENT_REQUEST add constraint FK_TSADV_RETIREMENT_REQUEST_PERSON_GROUP_EXT foreign key (PERSON_GROUP_EXT_ID) references BASE_PERSON_GROUP(ID);
alter table TSADV_RETIREMENT_REQUEST add constraint FK_TSADV_RETIREMENT_REQUEST_RETIREMENT foreign key (RETIREMENT_ID) references TSADV_RETIREMENT(ID);
alter table TSADV_RETIREMENT_REQUEST add constraint FK_TSADV_RETIREMENT_REQUEST_FILE foreign key (FILE_ID) references SYS_FILE(ID);
alter table TSADV_RETIREMENT_REQUEST add constraint FK_TSADV_RETIREMENT_REQUEST_REQUEST_STATUS foreign key (REQUEST_STATUS_ID) references TSADV_DIC_REQUEST_STATUS(ID);
create index IDX_TSADV_RETIREMENT_REQUEST_RETIREMENT_TYPE on TSADV_RETIREMENT_REQUEST (RETIREMENT_TYPE_ID);
create index IDX_TSADV_RETIREMENT_REQUEST_PERSON_GROUP_EXT on TSADV_RETIREMENT_REQUEST (PERSON_GROUP_EXT_ID);
create index IDX_TSADV_RETIREMENT_REQUEST_RETIREMENT on TSADV_RETIREMENT_REQUEST (RETIREMENT_ID);
create index IDX_TSADV_RETIREMENT_REQUEST_FILE on TSADV_RETIREMENT_REQUEST (FILE_ID);
create index IDX_TSADV_RETIREMENT_REQUEST_REQUEST_STATUS on TSADV_RETIREMENT_REQUEST (REQUEST_STATUS_ID);
