alter table TSADV_DIC_COST_CENTER add constraint FK_TSADV_DIC_COST_CENTER_COMPANY foreign key (COMPANY_ID) references BASE_DIC_COMPANY(ID);
create index IDX_TSADV_DIC_COST_CENTER_COMPANY on TSADV_DIC_COST_CENTER (COMPANY_ID);
