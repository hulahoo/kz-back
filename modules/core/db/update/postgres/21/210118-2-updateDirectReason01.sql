alter table TSADV_DIRECT_REASON add constraint FK_TSADV_DIRECT_REASON_COMPANY foreign key (COMPANY_ID) references BASE_DIC_COMPANY(ID);
create index IDX_TSADV_DIRECT_REASON_COMPANY on TSADV_DIRECT_REASON (COMPANY_ID);
