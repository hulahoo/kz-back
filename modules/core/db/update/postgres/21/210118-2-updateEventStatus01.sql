alter table TSADV_EVENT_STATUS add constraint FK_TSADV_EVENT_STATUS_COMPANY foreign key (COMPANY_ID) references BASE_DIC_COMPANY(ID);
create index IDX_TSADV_EVENT_STATUS_COMPANY on TSADV_EVENT_STATUS (COMPANY_ID);
