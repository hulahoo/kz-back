alter table TSADV_JOB_GROUP add constraint FK_TSADV_JOB_GROUP_JOB_CATEGORY foreign key (JOB_CATEGORY_ID) references TSADV_DIC_JOB_CATEGORY(ID);
create index IDX_TSADV_JOB_GROUP_JOB_CATEGORY on TSADV_JOB_GROUP (JOB_CATEGORY_ID);