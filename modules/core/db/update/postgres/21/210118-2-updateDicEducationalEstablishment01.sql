alter table TSADV_DIC_EDUCATIONAL_ESTABLISHMENT add constraint FK_TSADV_DIC_EDUCATIONAL_ESTABLISHMENT_COMPANY foreign key (COMPANY_ID) references BASE_DIC_COMPANY(ID);
create index IDX_TSADV_DIC_EDUCATIONAL_ESTABLISHMENT_COMPANY on TSADV_DIC_EDUCATIONAL_ESTABLISHMENT (COMPANY_ID);
