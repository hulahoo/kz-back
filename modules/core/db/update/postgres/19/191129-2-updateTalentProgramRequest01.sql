alter table TSADV_TALENT_PROGRAM_REQUEST add constraint FK_TSADV_TALENT_PROGRAM_REQUEST_CURRENT_STEP foreign key (CURRENT_STEP_ID) references TSADV_DIC_TALENT_PROGRAM_STEP(ID);
create index if not exists IDX_TSADV_TALENT_PROGRAM_REQUEST_CURRENT_STEP on TSADV_TALENT_PROGRAM_REQUEST (CURRENT_STEP_ID);
