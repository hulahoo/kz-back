alter table TSADV_DIC_QUALITY add constraint FK_TSADV_DIC_QUALITY_COMPANY foreign key (COMPANY_ID) references BASE_DIC_COMPANY(ID);
create index IDX_TSADV_DIC_QUALITY_COMPANY on TSADV_DIC_QUALITY (COMPANY_ID);
