alter table TSADV_DIC_SUITABILITY_TO_MILITARY add constraint FK_TSADV_DIC_SUITABILITY_TO_MILITARY_COMPANY foreign key (COMPANY_ID) references BASE_DIC_COMPANY(ID);
create index IDX_TSADV_DIC_SUITABILITY_TO_MILITARY_COMPANY on TSADV_DIC_SUITABILITY_TO_MILITARY (COMPANY_ID);
