alter table TSADV_JOB_GROUP add constraint FK_TSADV_JOB_GROUP_EMPLOYEE_CATEGORY foreign key (EMPLOYEE_CATEGORY_ID) references TSADV_DIC_EMPLOYEE_CATEGORY(ID);
create index IDX_TSADV_JOB_GROUP_EMPLOYEE_CATEGORY on TSADV_JOB_GROUP (EMPLOYEE_CATEGORY_ID);