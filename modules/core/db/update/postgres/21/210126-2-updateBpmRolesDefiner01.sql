alter table TSADV_BPM_ROLES_DEFINER add constraint FK_TSADV_BPM_ROLES_DEFINER_COMPANY foreign key (COMPANY_ID) references BASE_DIC_COMPANY(ID);
create index IDX_TSADV_BPM_ROLES_DEFINER_COMPANY on TSADV_BPM_ROLES_DEFINER (COMPANY_ID);