alter table TSADV_DIC_EDUCATION_DEGREE add constraint FK_TSADV_DIC_EDUCATION_DEGREE_COMPANY foreign key (COMPANY_ID) references BASE_DIC_COMPANY(ID);
create index IDX_TSADV_DIC_EDUCATION_DEGREE_COMPANY on TSADV_DIC_EDUCATION_DEGREE (COMPANY_ID);
