alter table TSADV_ASSIGNED_PERFORMANCE_PLAN add constraint FK_TSADV_ASSIGNED_PERFORMANCE_PLAN_STATUS foreign key (STATUS_ID) references TSADV_DIC_REQUEST_STATUS(ID);
create index IDX_TSADV_ASSIGNED_PERFORMANCE_PLAN_STATUS on TSADV_ASSIGNED_PERFORMANCE_PLAN (STATUS_ID);