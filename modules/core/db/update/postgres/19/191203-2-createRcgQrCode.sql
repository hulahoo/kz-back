alter table TSADV_RCG_QR_CODE add constraint FK_TSADV_RCG_QR_CODE_IMAGE foreign key (IMAGE_ID) references SYS_FILE(ID);
alter table TSADV_RCG_QR_CODE add constraint FK_TSADV_RCG_QR_CODE_PERSON_GROUP foreign key (PERSON_GROUP_ID) references BASE_PERSON_GROUP(ID);
create index if not exists IDX_TSADV_RCG_QR_CODE_PERSON_GROUP on TSADV_RCG_QR_CODE (PERSON_GROUP_ID);
