alter table TSADV_DIC_ATTESTATION_EVENT add constraint FK_TSADV_DIC_ATTESTATION_EVENT_COMPANY foreign key (COMPANY_ID) references BASE_DIC_COMPANY(ID);
create index IDX_TSADV_DIC_ATTESTATION_EVENT_COMPANY on TSADV_DIC_ATTESTATION_EVENT (COMPANY_ID);
