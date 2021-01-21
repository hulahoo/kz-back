alter table TSADV_GRADE add constraint FK_TSADV_GRADE_COMPANY foreign key (COMPANY_ID) references BASE_DIC_COMPANY(ID);
create index IDX_TSADV_GRADE_COMPANY on TSADV_GRADE (COMPANY_ID);
