alter table TSADV_ORG_STRUCTURE_REQUEST add constraint FK_TSADV_ORG_STRUCTURE_REQUEST_REQUEST_STATUS foreign key (REQUEST_STATUS_ID) references TSADV_DIC_REQUEST_STATUS(ID);
alter table TSADV_ORG_STRUCTURE_REQUEST add constraint FK_TSADV_ORG_STRUCTURE_REQUEST_COMPANY foreign key (COMPANY_ID) references TSADV_DIC_COMPANY(ID);
alter table TSADV_ORG_STRUCTURE_REQUEST add constraint FK_TSADV_ORG_STRUCTURE_REQUEST_DEPARTMENT foreign key (DEPARTMENT_ID) references BASE_ORGANIZATION_GROUP(ID);
alter table TSADV_ORG_STRUCTURE_REQUEST add constraint FK_TSADV_ORG_STRUCTURE_REQUEST_AUTHOR foreign key (AUTHOR_ID) references BASE_PERSON_GROUP(ID);
create index IDX_TSADV_ORG_STRUCTURE_REQUEST_REQUEST_STATUS on TSADV_ORG_STRUCTURE_REQUEST (REQUEST_STATUS_ID);
create index IDX_TSADV_ORG_STRUCTURE_REQUEST_COMPANY on TSADV_ORG_STRUCTURE_REQUEST (COMPANY_ID);
create index IDX_TSADV_ORG_STRUCTURE_REQUEST_DEPARTMENT on TSADV_ORG_STRUCTURE_REQUEST (DEPARTMENT_ID);
create index IDX_TSADV_ORG_STRUCTURE_REQUEST_AUTHOR on TSADV_ORG_STRUCTURE_REQUEST (AUTHOR_ID);
