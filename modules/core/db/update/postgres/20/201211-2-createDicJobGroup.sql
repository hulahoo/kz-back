alter table TSADV_DIC_JOB_GROUP add constraint FK_TSADV_DIC_JOB_GROUP_JOB_GROUP foreign key (JOB_GROUP_ID) references TSADV_JOB_GROUP(ID);
create index IDX_TSADV_DIC_JOB_GROUP_JOB_GROUP on TSADV_DIC_JOB_GROUP (JOB_GROUP_ID);
