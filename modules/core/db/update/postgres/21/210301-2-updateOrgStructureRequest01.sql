alter table TSADV_ORG_STRUCTURE_REQUEST add constraint FK_TSADV_ORG_STRUCTURE_REQUEST_COMPANY foreign key (COMPANY_ID) references BASE_DIC_COMPANY(ID);
create index IDX_TSADV_ORG_STRUCTURE_REQUEST_COMPANY on TSADV_ORG_STRUCTURE_REQUEST (COMPANY_ID);
