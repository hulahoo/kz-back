alter table TSADV_REASON_NO_PRODUCTION_CONNECTION add constraint FK_TSADV_REASON_NO_PRODUCTION_CONNECTION_COMPANY foreign key (COMPANY_ID) references BASE_DIC_COMPANY(ID);
create index IDX_TSADV_REASON_NO_PRODUCTION_CONNECTION_COMPANY on TSADV_REASON_NO_PRODUCTION_CONNECTION (COMPANY_ID);