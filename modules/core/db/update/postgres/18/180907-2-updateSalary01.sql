alter table TSADV_SALARY add constraint FK_TSADV_SALARY_REASON foreign key (REASON_ID) references TSADV_DIC_SALARY_CHANGE_REASON(ID);
create index IDX_TSADV_SALARY_REASON on TSADV_SALARY (REASON_ID);
