alter table TSADV_SUBSECTIONS add constraint FK_TSADV_SUBSECTIONS_COMPANY foreign key (COMPANY_ID) references BASE_DIC_COMPANY(ID);
create index IDX_TSADV_SUBSECTIONS_COMPANY on TSADV_SUBSECTIONS (COMPANY_ID);
