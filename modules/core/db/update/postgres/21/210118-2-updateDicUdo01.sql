alter table TSADV_DIC_UDO add constraint FK_TSADV_DIC_UDO_COMPANY foreign key (COMPANY_ID) references BASE_DIC_COMPANY(ID);
create index IDX_TSADV_DIC_UDO_COMPANY on TSADV_DIC_UDO (COMPANY_ID);
