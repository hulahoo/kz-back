alter table TSADV_DIC_HR_ROLE add constraint FK_TSADV_DIC_HR_ROLE_COMPANY foreign key (COMPANY_ID) references BASE_DIC_COMPANY(ID);
create index IDX_TSADV_DIC_HR_ROLE_COMPANY on TSADV_DIC_HR_ROLE (COMPANY_ID);