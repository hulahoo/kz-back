alter table TSADV_INSURANCE_CONTRACT add constraint FK_TSADV_INSURANCE_CONTRACT_COMPANY foreign key (COMPANY_ID) references TSADV_DIC_COMPANY(ID);
alter table TSADV_INSURANCE_CONTRACT add constraint FK_TSADV_INSURANCE_CONTRACT_DEFAULT_DOCUMENT_TYPE foreign key (DEFAULT_DOCUMENT_TYPE_ID) references TSADV_DIC_DOCUMENT_TYPE(ID);
alter table TSADV_INSURANCE_CONTRACT add constraint FK_TSADV_INSURANCE_CONTRACT_DEFAULT_ADDRESS foreign key (DEFAULT_ADDRESS_ID) references TSADV_DIC_ADDRESS_TYPE(ID);
create index IDX_TSADV_INSURANCE_CONTRACT_COMPANY on TSADV_INSURANCE_CONTRACT (COMPANY_ID);
create index IDX_TSADV_INSURANCE_CONTRACT_DEFAULT_DOCUMENT_TYPE on TSADV_INSURANCE_CONTRACT (DEFAULT_DOCUMENT_TYPE_ID);
create index IDX_TSADV_INSURANCE_CONTRACT_DEFAULT_ADDRESS on TSADV_INSURANCE_CONTRACT (DEFAULT_ADDRESS_ID);
