alter table TSADV_DIC_EMPLOYEE_CATEGORY add constraint FK_TSADV_DIC_EMPLOYEE_CATEGORY_COMPANY foreign key (COMPANY_ID) references BASE_DIC_COMPANY(ID);
create index IDX_TSADV_DIC_EMPLOYEE_CATEGORY_COMPANY on TSADV_DIC_EMPLOYEE_CATEGORY (COMPANY_ID);
