alter table TSADV_DIC_REQUISITION_ACCESS_LEVEL add constraint FK_TSADV_DIC_REQUISITION_ACCESS_LEVEL_COMPANY foreign key (COMPANY_ID) references BASE_DIC_COMPANY(ID);
create index IDX_TSADV_DIC_REQUISITION_ACCESS_LEVEL_COMPANY on TSADV_DIC_REQUISITION_ACCESS_LEVEL (COMPANY_ID);
