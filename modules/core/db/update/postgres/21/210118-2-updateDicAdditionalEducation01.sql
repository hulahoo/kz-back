alter table TSADV_DIC_ADDITIONAL_EDUCATION add constraint FK_TSADV_DIC_ADDITIONAL_EDUCATION_COMPANY foreign key (COMPANY_ID) references BASE_DIC_COMPANY(ID);
create index IDX_TSADV_DIC_ADDITIONAL_EDUCATION_COMPANY on TSADV_DIC_ADDITIONAL_EDUCATION (COMPANY_ID);
