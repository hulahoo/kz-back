alter table TSADV_DIC_SOURCE add constraint FK_TSADV_DIC_SOURCE_COMPANY foreign key (COMPANY_ID) references BASE_DIC_COMPANY(ID);
create index IDX_TSADV_DIC_SOURCE_COMPANY on TSADV_DIC_SOURCE (COMPANY_ID);