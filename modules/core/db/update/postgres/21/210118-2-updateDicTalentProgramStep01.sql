alter table TSADV_DIC_TALENT_PROGRAM_STEP add constraint FK_TSADV_DIC_TALENT_PROGRAM_STEP_COMPANY foreign key (COMPANY_ID) references BASE_DIC_COMPANY(ID);
create index IDX_TSADV_DIC_TALENT_PROGRAM_STEP_COMPANY on TSADV_DIC_TALENT_PROGRAM_STEP (COMPANY_ID);