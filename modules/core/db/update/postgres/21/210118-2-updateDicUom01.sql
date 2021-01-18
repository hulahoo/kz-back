alter table TSADV_DIC_UOM add constraint FK_TSADV_DIC_UOM_COMPANY foreign key (COMPANY_ID) references BASE_DIC_COMPANY(ID);
create index IDX_TSADV_DIC_UOM_COMPANY on TSADV_DIC_UOM (COMPANY_ID);
