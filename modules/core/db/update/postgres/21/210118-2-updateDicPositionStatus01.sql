alter table TSADV_DIC_POSITION_STATUS add constraint FK_TSADV_DIC_POSITION_STATUS_COMPANY foreign key (COMPANY_ID) references BASE_DIC_COMPANY(ID);
create index IDX_TSADV_DIC_POSITION_STATUS_COMPANY on TSADV_DIC_POSITION_STATUS (COMPANY_ID);
