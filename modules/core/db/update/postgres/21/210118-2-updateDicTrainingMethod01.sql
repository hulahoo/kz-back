alter table TSADV_DIC_TRAINING_METHOD add constraint FK_TSADV_DIC_TRAINING_METHOD_COMPANY foreign key (COMPANY_ID) references BASE_DIC_COMPANY(ID);
create index IDX_TSADV_DIC_TRAINING_METHOD_COMPANY on TSADV_DIC_TRAINING_METHOD (COMPANY_ID);