alter table TSADV_CERTIFICATE_REQUEST drop constraint if exists FK_TSADV_CERTIFICATE_REQUEST_PERSON_GROUP;
alter table TSADV_CERTIFICATE_REQUEST add constraint FK_TSADV_CERTIFICATE_REQUEST_PERSON_GROUP foreign key (PERSON_GROUP_ID) references BASE_PERSON_GROUP(ID);
create index if not exists IDX_TSADV_CERTIFICATE_REQUEST_PERSON_GROUP on TSADV_CERTIFICATE_REQUEST (PERSON_GROUP_ID);
