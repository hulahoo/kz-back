alter table TSADV_DIC_PAYROLL add constraint FK_TSADV_DIC_PAYROLL_COMPANY foreign key (COMPANY_ID) references BASE_DIC_COMPANY(ID);
create index IDX_TSADV_DIC_PAYROLL_COMPANY on TSADV_DIC_PAYROLL (COMPANY_ID);
