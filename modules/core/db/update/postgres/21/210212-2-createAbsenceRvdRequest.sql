alter table TSADV_ABSENCE_RVD_REQUEST add constraint FK_TSADV_ABSENCE_RVD_REQUEST_PERSON_GROUP foreign key (PERSON_GROUP_ID) references BASE_PERSON_GROUP(ID);
alter table TSADV_ABSENCE_RVD_REQUEST add constraint FK_TSADV_ABSENCE_RVD_REQUEST_TYPE foreign key (TYPE_ID) references TSADV_DIC_ABSENCE_TYPE(ID);
alter table TSADV_ABSENCE_RVD_REQUEST add constraint FK_TSADV_ABSENCE_RVD_REQUEST_PURPOSE foreign key (PURPOSE_ID) references TSADV_DIC_ABSENCE_PURPOSE(ID);
alter table TSADV_ABSENCE_RVD_REQUEST add constraint FK_TSADV_ABSENCE_RVD_REQUEST_STATUS foreign key (STATUS_ID) references TSADV_DIC_REQUEST_STATUS(ID);
create index IDX_TSADV_ABSENCE_RVD_REQUEST_PERSON_GROUP on TSADV_ABSENCE_RVD_REQUEST (PERSON_GROUP_ID);
create index IDX_TSADV_ABSENCE_RVD_REQUEST_TYPE on TSADV_ABSENCE_RVD_REQUEST (TYPE_ID);
create index IDX_TSADV_ABSENCE_RVD_REQUEST_PURPOSE on TSADV_ABSENCE_RVD_REQUEST (PURPOSE_ID);
create index IDX_TSADV_ABSENCE_RVD_REQUEST_STATUS on TSADV_ABSENCE_RVD_REQUEST (STATUS_ID);
