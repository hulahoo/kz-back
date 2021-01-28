alter table TSADV_ATTACHMENT add constraint FK_TSADV_ATTACHMENT_INSURANCE_CONTRACT foreign key (INSURANCE_CONTRACT_ID) references TSADV_INSURANCE_CONTRACT(ID);
create index IDX_TSADV_ATTACHMENT_INSURANCE_CONTRACT on TSADV_ATTACHMENT (INSURANCE_CONTRACT_ID);