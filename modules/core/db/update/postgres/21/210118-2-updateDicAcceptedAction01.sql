alter table TSADV_DIC_ACCEPTED_ACTION add constraint FK_TSADV_DIC_ACCEPTED_ACTION_COMPANY foreign key (COMPANY_ID) references BASE_DIC_COMPANY(ID);
create index IDX_TSADV_DIC_ACCEPTED_ACTION_COMPANY on TSADV_DIC_ACCEPTED_ACTION (COMPANY_ID);
